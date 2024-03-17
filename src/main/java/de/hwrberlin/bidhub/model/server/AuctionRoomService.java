package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ClientApplication;
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
    private volatile boolean isStarted = false;
    private final HashMap<WebSocket, ClientInfo> registeredClients = new HashMap<>();
    private final ArrayList<Runnable> closeRoomHooks = new ArrayList<>();
    private final AuctionRoomInfo info;
    private Pair<WebSocket, ClientInfo> initiator;
    private final ArrayList<String> bannedClients = new ArrayList<>();

    private AuctionInfo currentAuctionInfo = null;
    //private float currentBid = 0;
    //private Pair<WebSocket, String> currentBidClient = null;

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
                    Thread.sleep(1000);
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
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_AuctionRoomKickClient.name() + info.getId(), this::handleKickRequest);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_AuctionRoomBanClient.name() + info.getId(), this::handleBanRequest);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_AuctionRoomStartAuction.name() + info.getId(), this::onStartAuctionRequest);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_AuctionRoomOnBidRequest.name() +info.getId(), this::onBidRequest);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_GetAuctionInfoRequest.name() + info.getId(), this::onGetAuctionInfoRequest);
    }

    private void unregisterCallbacks(){
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_ReceiveChatMessageFromClient.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_UnregisterClient.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_RegisterClient.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_GetIsInitiator.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_GetAuctionRoomInfo.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_ValidateRoomPassword.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_AuctionRoomKickClient.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_AuctionRoomBanClient.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_AuctionRoomStartAuction.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_AuctionRoomOnBidRequest.name() + info.getId());
        ServerApplication.getSocketManager().unregisterCallback(CallbackType.Server_GetAuctionInfoRequest.name() + info.getId());
    }

    public synchronized void stop(){
        isStarted = false;

        for (Map.Entry<WebSocket, ClientInfo> entry: registeredClients.entrySet()){
            entry.getKey().send(new JsonMessage(CallbackType.Client_OnRoomClosed.name(),
                    new RoomClosedResponseData("closed"),
                    RoomClosedResponseData.class.getName()).toJson());
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

        if (bannedClients.contains(clientInfo.getUsername())){
            System.out.println("Der Benutzer " + clientInfo.getUsername() + " hat versucht sich im Auction Room " + info.getId() + " anzumelden obwohl er gebannt ist.");
            JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(false), SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId());
            context.conn().send(msg.toJson());
            return;
        }

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
        if (currentAuctionInfo == null)
            return;

        int remainingSeconds = currentAuctionInfo.reduceRemainingSeconds();

        if (remainingSeconds == 0){
            finishAuction();
        }
        else{
            JsonMessage msg = new JsonMessage(CallbackType.Client_OnTick.name(), new AuctionRoomTickData(currentAuctionInfo.getRemainingSeconds()), AuctionRoomTickData.class.getName());
            sendJsonToAllClients(msg.toJson());
        }
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

    private synchronized void handleKickRequest(CallbackContext context){
        if (!context.conn().equals(initiator.getKey())){
            System.out.println("Nur der Initiator darf Benutzer kicken!");
            return;
        }

        KickBanRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Fehler bei der Konvertierung eines Kick-Requests im Auction Room " + info.getId() + "!");
            throw new RuntimeException(e);
        }

        kickClient(data.username());
    }

    private synchronized void handleBanRequest(CallbackContext context){
        if (!context.conn().equals(initiator.getKey())){
            System.out.println("Nur der Initiator darf Benutzer bannen!");
            return;
        }

        KickBanRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Fehler bei der Konvertierung eines Ban-Requests im Auction Room " + info.getId() + "!");
            throw new RuntimeException(e);
        }

        banClient(data.username());
    }

    private synchronized void kickClient(String username){
        if (initiator.getValue().getUsername().equals(username)){
            System.out.println("Der Initiator kann nicht gekicked werden!");
            return;
        }

        for (Map.Entry<WebSocket, ClientInfo> entry : registeredClients.entrySet()){
            if (entry.getValue().getUsername().equals(username)){
                entry.getKey().send(new JsonMessage(CallbackType.Client_OnRoomClosed.name(),
                        new RoomClosedResponseData("kick"),
                        RoomClosedResponseData.class.getName()).toJson());
            }
        }
    }

    private synchronized void banClient(String username){
        if (initiator.getValue().getUsername().equals(username)){
            System.out.println("Der Initiator kann nicht gebannt werden!");
            return;
        }

        for (Map.Entry<WebSocket, ClientInfo> entry : registeredClients.entrySet()){
            if (entry.getValue().getUsername().equals(username)){
                bannedClients.add(entry.getValue().getUsername());
                entry.getKey().send(new JsonMessage(CallbackType.Client_OnRoomClosed.name(),
                        new RoomClosedResponseData("ban"),
                        RoomClosedResponseData.class.getName()).toJson());
            }
        }
    }

    private synchronized void onStartAuctionRequest(CallbackContext context){
        boolean success = false;

        if (currentAuctionInfo == null){
            AuctionInfo info;
            try {
                info = context.message().getData();
                startAuction(info);
                success = true;
            } catch (Exception e) {
                System.out.println("Fehler beim Konvertieren der AuctionInfo!");
            }
        }

        JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(success), SuccessResponseData.class.getName());
        context.conn().send(msg.setResponseId(context.message().getMessageId()).toJson());
    }

    private synchronized void startAuction(AuctionInfo info){
        currentAuctionInfo = info;
        currentAuctionInfo.setBidData(null);

        JsonMessage msg = new JsonMessage(CallbackType.Client_OnAuctionStarted.name(), currentAuctionInfo, AuctionInfo.class.getName());
        sendJsonToAllClients(msg.toJson());
        sendChatMessageToClients(new ChatMessageResponseData("Eine Auktion wurde gestartet!", "SYSTEM", Helpers.getCurrentTime(), true, ""));
    }

    private synchronized void finishAuction(){
        String username = "";
        String bidInfo = " Kein Benutzer hat das Produkt ersteigert, da keine Gebote abgegeben wurden.";

        if (currentAuctionInfo.getBidData() != null){
            username = currentAuctionInfo.getBidData().username();
            bidInfo = " Der Benutzer " + username + " hat das Produkt für " + Helpers.formatToEuro(currentAuctionInfo.getBidData().bid()) + " ersteigert.";
        }

        JsonMessage msg = new JsonMessage(CallbackType.Client_OnAuctionFinished.name());
        sendJsonToAllClients(msg.toJson());

        sendChatMessageToClients(new ChatMessageResponseData("Die Auktion ist abgeschlossen!" + bidInfo,
                "SYSTEM", Helpers.getCurrentTime(), true, ""));

        currentAuctionInfo = null;
    }

    private synchronized void onBidRequest(CallbackContext context){
        boolean success = false;
        AuctionRoomBidData data = null;
        try {
            data = context.message().getData();
            float currentBid = 0;

            if (currentAuctionInfo.getBidData() != null)
                currentBid = currentAuctionInfo.getBidData().bid();

            if (data.bid() >= currentBid + currentAuctionInfo.getMinimumIncrement() && data.bid() >= currentAuctionInfo.getMinimumBid()){
                for (Map.Entry<WebSocket, ClientInfo> entry : registeredClients.entrySet()){
                    if (data.username().equals(entry.getValue().getUsername())){
                        currentAuctionInfo.setBidData(data);
                        success = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren der Bid Data");
        }

        JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(success), SuccessResponseData.class.getName());
        context.conn().send(msg.setResponseId(context.message().getMessageId()).toJson());

        if (success){
            sendChatMessageToClients(new ChatMessageResponseData(data.username() + " hat ein Gebot abgegeben: " + Helpers.formatToEuro(data.bid()),
                    "SYSTEM", Helpers.getCurrentTime(), true, ""));

            JsonMessage bidMsg = new JsonMessage(CallbackType.Client_OnBid.name(), data, AuctionRoomBidData.class.getName());
            sendJsonToAllClients(bidMsg.toJson());
        }
    }

    private synchronized void sendJsonToAllClients(String json){
        for (Map.Entry<WebSocket, ClientInfo> entry : registeredClients.entrySet()){
            entry.getKey().send(json);
        }
    }

    private synchronized void onGetAuctionInfoRequest(CallbackContext context){
        JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), currentAuctionInfo, AuctionInfo.class.getName());
        context.conn().send(msg.setResponseId(context.message().getMessageId()).toJson());
    }
}
