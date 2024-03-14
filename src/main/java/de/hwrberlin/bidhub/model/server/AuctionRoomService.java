package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.*;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.Pair;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuctionRoomService {
    public static final int TICKS_PER_SECOND = 1;
    private volatile boolean isStarted = false;
    private final HashMap<WebSocket, ClientInfo> registeredClients = new HashMap<>();
    private final ArrayList<Runnable> closeRoomHooks = new ArrayList<>();
    private final AuctionRoomInfo info;
    private Pair<WebSocket, ClientInfo> initiator;

    public AuctionRoomService(AuctionRoomInfo info){
        this.info = info;
    }

    public synchronized void start() {
        isStarted = true;
        handleLifecycle();
        registerCallbacks();
        System.out.println("Auction Room " + info.getId() + " wurde geöffnet!");
    }

    private void handleLifecycle(){
        new Thread(() -> {
            while (isStarted){
                try {
                    Thread.sleep(1000 / TICKS_PER_SECOND);
                    onTick();
                }
                catch (InterruptedException e) {
                    stop();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
    }

    public synchronized Pair<WebSocket, ClientInfo> getInitiator() {
        return initiator;
    }

    public synchronized AuctionRoomInfo getInfo() {
        return info;
    }

    private void registerCallbacks(){
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_ReceiveChatMessageFromClient.name() + info.getId(), this::receiveChatMessageFromClient);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_UnregisterClient.name() + info.getId(), this::unregisterClient);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_RegisterClient.name() + info.getId(), this::registerClient);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_GetIsInitiator.name() + info.getId(), this::getInitiatorRequest);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_GetAuctionRoomInfo.name() + info.getId(), this::getAuctionRoomInfoRequest);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_ValidateRoomPassword.name() + info.getId(), this::validatePasswordRequest);
    }

    private void unregisterCallbacks(){
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_ReceiveChatMessageFromClient.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_UnregisterClient.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_RegisterClient.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_GetIsInitiator.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_GetAuctionRoomInfo.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_ValidateRoomPassword.name() + info.getId());
    }

    public synchronized void stop(){
        isStarted = false;

        for (Map.Entry<WebSocket, ClientInfo> entry: registeredClients.entrySet()){
            entry.getKey().send(new JsonMessage(CallbackType.Client_OnRoomClosed.name()).toJson());
        }

        unregisterCallbacks();

        for (Runnable runnable : closeRoomHooks){
            runnable.run();
        }

        System.out.println("Auction Room " + info.getId() + " wurde geschlossen!");
    }

    public synchronized void registerClient(CallbackContext context){
        AuctionRoomRegisterClientRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Fehler beim registrieren eines Clients für einen Auction Room aufgetreten!");
            JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(false), SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId());
            context.conn().send(msg.toJson());
            throw new RuntimeException(e);
        }

        ClientInfo clientInfo = new ClientInfo(context.conn(), data.username(), info.getClients().isEmpty());

        registeredClients.put(clientInfo.getConnection(), clientInfo);
        info.addClient(clientInfo.getUsername(), clientInfo.isInitiator());

        if (clientInfo.isInitiator())
            initiator = new Pair<>(clientInfo.getConnection(), clientInfo);

        JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(true), SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId());
        context.conn().send(msg.toJson());

        sendChatMessageToClients(new ChatMessageResponseData(
                "Der Benutzer " + clientInfo.getUsername() + " ist der Auktion beigetreten!",
                "SYSTEM",
                Helpers.getCurrentTime(),
                true,
                ""
        ));

        System.out.println("Client " + clientInfo.getUsername() + " ist dem Auction Room " + info.getId() + " beigetreten.");
    }

    private synchronized void unregisterClient(ClientInfo clientInfo){
        registeredClients.remove(clientInfo.getConnection());
        info.removeClient(clientInfo.getUsername());

        sendChatMessageToClients(new ChatMessageResponseData(
                "Der Benutzer " + clientInfo.getUsername() + " hat die Auktion verlassen!",
                "SYSTEM",
                Helpers.getCurrentTime(),
                true,
                ""
        ));

        System.out.println("Client " + clientInfo.getUsername() + " hat den Auction Room " + info.getId() + " verlassen.");

        if (clientInfo.isInitiator()){
            initiator = null;

            if (isStarted)
                stop();
        }
    }

    public synchronized void unregisterClient (CallbackContext context){
        ClientInfo clientInfo = getClientInfoByConnection(context.conn());

        if (clientInfo == null){
            System.out.println("Der Client " + context.conn() + " ist nicht im Raum registriert, daher kann er nicht unregistriert werden.");
            return;
        }

        unregisterClient(clientInfo);
    }

    private synchronized void validatePasswordRequest(CallbackContext context){
        AuctionRoomPasswordValidationRequestData data;
        boolean success = false;
        try {
            data = context.message().getData();
            success = info.getPassword().equals(data.hashedPassword());
        } catch (Exception e) {
            System.out.println("Fehler beim validieren des Passworts für den Auction Room " + info.getId() + "!");
        }

        JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(success), SuccessResponseData.class.getName());
        context.conn().send(msg.setResponseId(context.message().getMessageId()).toJson());
    }

    public synchronized void receiveChatMessageFromClient(CallbackContext context){
        ChatMessageRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Fehler bei der Konvertierung einer Chat Nachricht im Auction Room " + info.getId() + "!");
            throw new RuntimeException(e);
        }

        ClientInfo clientInfo = getClientInfoByConnection(context.conn());

        if (clientInfo == null){
            System.out.println("Fehler: der Client " + context.conn() + " ist nicht im Raum registriert. Die Chat Nachricht wird nicht gesendet.");
            return;
        }

        ChatMessageResponseData responseData = new ChatMessageResponseData(data.message(), clientInfo.getUsername(), Helpers.getCurrentTime(), clientInfo.isInitiator() || !data.recipient().isBlank(), data.recipient());
        sendChatMessageToClients(responseData);
    }

    private synchronized void sendChatMessageToClients(ChatMessageResponseData messageResponseData){
        for (Map.Entry<WebSocket, ClientInfo> entry : registeredClients.entrySet()){
            if (messageResponseData.recipient().isBlank() ||
                messageResponseData.recipient().equals(entry.getValue().getUsername()) ||
                messageResponseData.senderUsername().equals(entry.getValue().getUsername())){
                    entry.getKey().send(new JsonMessage(CallbackType.Client_ReceiveChatMessage.name(), messageResponseData, ChatMessageResponseData.class.getName()).toJson());
            }
        }

        System.out.println("Chat Nachricht im Auction Room " + info.getId() + " vom Client " + messageResponseData.senderUsername() + " gesendet. (Privatnachricht: " + !messageResponseData.recipient().isBlank() + ")");
    }

    private synchronized ClientInfo getClientInfoByConnection(WebSocket conn){
        return registeredClients.get(conn);
    }

    private synchronized void onTick(){

    }

    public synchronized void addCloseRoomHook(Runnable hook){
        closeRoomHooks.add(hook);
    }

    private synchronized void getInitiatorRequest(CallbackContext context){
        WebSocket initiator = getInitiator().getKey();

        SuccessResponseData responseData = new SuccessResponseData(context.conn().equals(initiator));
        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), responseData, SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }

    private synchronized void getAuctionRoomInfoRequest(CallbackContext context){
        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), getInfo(), AuctionRoomInfo.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }
}
