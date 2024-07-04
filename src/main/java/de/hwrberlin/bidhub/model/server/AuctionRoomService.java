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

/**
 * Verwaltet einen Auktionsraum, einschließlich der Registrierung von Clients, dem Senden von Chat-Nachrichten,
 * der Verwaltung von Auktionen und dem Handhaben von Client-Aktionen wie Beitritt, Verlassen, Bieten und mehr.
 */
public class AuctionRoomService {
    private volatile boolean isStarted = false;
    private final HashMap<WebSocket, ClientInfo> registeredClients = new HashMap<>();
    private final ArrayList<Runnable> closeRoomHooks = new ArrayList<>();
    private final AuctionRoomInfo info;
    private Pair<WebSocket, ClientInfo> initiator;
    private final ArrayList<Long> bannedClientsIds = new ArrayList<>();

    private AuctionInfo currentAuctionInfo = null;

    /**
     * Konstruktor, der eine neue Instanz eines Auktionsraums mit spezifischen Informationen initialisiert.
     *
     * @param info Die Informationen des Auktionsraums, einschließlich ID, Titel, Beschreibung und Passwort.
     */
    public AuctionRoomService(AuctionRoomInfo info){
        this.info = info;
    }

    /**
     * Startet den Auktionsraum, indem der Lebenszyklus-Thread initialisiert und Callbacks registriert werden.
     * Markiert den Auktionsraum als gestartet und informiert über den Öffnungsstatus.
     */
    public synchronized void start() {
        isStarted = true;
        handleLifecycle();
        registerCallbacks();
        System.out.println("Auction Room " + info.getId() + " wurde geöffnet!");
    }

    /**
     * Verwaltet den Lebenszyklus des Auktionsraums in einem separaten Thread.
     * Führt periodische Aktionen aus, solange der Auktionsraum aktiv ist.
     */
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

    /**
     * Gibt den Initiator des Auktionsraums zurück.
     *
     * @return Ein Paar aus WebSocket und ClientInfo des Initiators.
     */
    public synchronized Pair<WebSocket, ClientInfo> getInitiator() {
        return initiator;
    }

    /**
     * Gibt die Informationen des Auktionsraums zurück.
     *
     * @return Die Informationen des Auktionsraums.
     */
    public synchronized AuctionRoomInfo getInfo() {
        return info;
    }

    /**
     * Registriert Callbacks für verschiedene Aktionen und Anfragen, die im Auktionsraum auftreten können.
     */
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

    /**
     * Hebt die Registrierung aller Callbacks auf, die für den Auktionsraum gesetzt wurden.
     */
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

    /**
     * Stoppt den Auktionsraum, informiert alle registrierten Clients über die Schließung,
     * hebt die Registrierung von Callbacks auf und führt alle hinterlegten Schließungshooks aus.
     */
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

    /**
     * Registriert einen Client im Auktionsraum, überprüft, ob der Client gebannt ist, und fügt ihn hinzu,
     * wenn er nicht gebannt ist. Sendet eine Bestätigungsnachricht an den Client.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält Informationen über die Nachricht und die Verbindung.
     */
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

        ClientInfo clientInfo = new ClientInfo(context.conn(), data.client(), info.getClients().isEmpty());

        if (bannedClientsIds.contains(clientInfo.getId())){
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

    /**
     * Entfernt einen Client aus dem Auktionsraum und informiert andere Clients über das Verlassen.
     *
     * @param clientInfo Die Informationen des Clients, der den Auktionsraum verlässt.
     */
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

    /**
     * Entfernt einen Client aus dem Auktionsraum und informiert andere Clients über das Verlassen.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält Informationen über die Nachricht und die Verbindung.
     */
    public synchronized void unregisterClient (CallbackContext context){
        ClientInfo clientInfo = getClientInfoByConnection(context.conn());

        if (clientInfo == null){
            System.out.println("Der Client " + context.conn() + " ist nicht im Raum registriert, daher kann er nicht unregistriert werden.");
            return;
        }

        unregisterClient(clientInfo);
    }

    /**
     * Überprüft das Passwort für den Zugang zum Auktionsraum und sendet eine Erfolgs- oder Fehlermeldung zurück.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält Informationen über die Nachricht und die Verbindung.
     */
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

    /**
     * Empfängt eine Chat-Nachricht von einem Client und sendet sie an alle anderen Clients im Auktionsraum.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält Informationen über die Nachricht und die Verbindung.
     */
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

    /**
     * Sendet eine Chat-Nachricht an alle Clients im Auktionsraum.
     *
     * @param messageResponseData Die Daten der Chat-Nachricht, die gesendet werden soll.
     */
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

    /**
     * Ermittelt die Client-Informationen basierend auf der WebSocket-Verbindung.
     *
     * @param conn Die WebSocket-Verbindung des Clients.
     * @return Die Informationen des Clients oder null, wenn der Client nicht gefunden wurde.
     */
    private synchronized ClientInfo getClientInfoByConnection(WebSocket conn){
        return registeredClients.get(conn);
    }

    /**
     * Wird periodisch aufgerufen, um den Zustand der aktuellen Auktion zu aktualisieren.
     * Überprüft, ob die Auktion noch läuft und aktualisiert die verbleibende Zeit.
     * Beendet die Auktion, wenn die Zeit abgelaufen ist.
     */
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

    /**
     * Fügt einen Hook hinzu, der ausgeführt wird, wenn der Auktionsraum geschlossen wird.
     * Dies ermöglicht es, benutzerdefinierte Aktionen vor dem endgültigen Schließen des Raums durchzuführen.
     *
     * @param hook Der auszuführende Hook (Codeblock) beim Schließen des Raums.
     */
    public synchronized void addCloseRoomHook(Runnable hook){
        closeRoomHooks.add(hook);
    }

    /**
     * Verarbeitet eine Anfrage, um zu überprüfen, ob der anfragende Client der Initiator des Auktionsraums ist.
     * Sendet eine Antwort mit dem Ergebnis zurück an den Client.
     *
     * @param context Der Kontext der Anfrage, enthält Informationen über die anfragende Verbindung.
     */
    private synchronized void getInitiatorRequest(CallbackContext context){
        WebSocket initiator = getInitiator().getKey();

        SuccessResponseData responseData = new SuccessResponseData(context.conn().equals(initiator));
        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), responseData, SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }

    /**
     * Verarbeitet eine Anfrage, um Informationen über den Auktionsraum zu erhalten.
     * Sendet die Informationen des Auktionsraums als Antwort zurück an den Client.
     *
     * @param context Der Kontext der Anfrage, enthält Informationen über die anfragende Verbindung.
     */
    private synchronized void getAuctionRoomInfoRequest(CallbackContext context){
        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), getInfo(), AuctionRoomInfo.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }

    /**
     * Verarbeitet eine Anfrage, um einen Benutzer aus dem Auktionsraum zu entfernen (kick).
     * Kann nur vom Initiator des Raums ausgeführt werden. Informiert den gekickten Benutzer über die Aktion.
     *
     * @param context Der Kontext der Anfrage, enthält Informationen über die anfragende Verbindung und die Daten des Kick-Requests.
     */
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

    /**
     * Verarbeitet eine Anfrage, um einen Benutzer dauerhaft aus dem Auktionsraum zu verbannen (ban).
     * Kann nur vom Initiator des Raums ausgeführt werden. Informiert den gebannten Benutzer über die Aktion.
     *
     * @param context Der Kontext der Anfrage, enthält Informationen über die anfragende Verbindung und die Daten des Ban-Requests.
     */
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

    /**
     * Entfernt einen Benutzer aus dem Auktionsraum und informiert ihn über den Kick.
     * Diese Methode wird intern aufgerufen, um die Logik des Kickens eines Benutzers zu handhaben.
     *
     * @param username Der Benutzername des zu kickenden Benutzers.
     */
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

    /**
     * Verbannen eines Benutzers aus dem Auktionsraum und informiert ihn über das Ban.
     * Diese Methode wird intern aufgerufen, um die Logik des Verbannens eines Benutzers zu handhaben.
     *
     * @param username Der Benutzername des zu bannenden Benutzers.
     */
    private synchronized void banClient(String username){
        if (initiator.getValue().getUsername().equals(username)){
            System.out.println("Der Initiator kann nicht gebannt werden!");
            return;
        }

        for (Map.Entry<WebSocket, ClientInfo> entry : registeredClients.entrySet()){
            if (entry.getValue().getUsername().equals(username)){
                bannedClientsIds.add(entry.getValue().getId());
                entry.getKey().send(new JsonMessage(CallbackType.Client_OnRoomClosed.name(),
                        new RoomClosedResponseData("ban"),
                        RoomClosedResponseData.class.getName()).toJson());
            }
        }
    }

    /**
     * Verarbeitet eine Anfrage zum Starten einer neuen Auktion im Auktionsraum.
     * Überprüft, ob bereits eine Auktion läuft, und startet eine neue, wenn keine aktive Auktion vorhanden ist.
     *
     * @param context Der Kontext der Anfrage, enthält Informationen über die anfragende Verbindung und die Daten der Auktion.
     */
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

    /**
     * Startet eine neue Auktion mit den gegebenen Auktionsinformationen.
     * Initialisiert die Auktion und informiert alle Clients im Raum über den Start der Auktion.
     *
     * @param info Die Informationen der zu startenden Auktion.
     */
    private synchronized void startAuction(AuctionInfo info){
        currentAuctionInfo = info;
        currentAuctionInfo.setBidData(null);

        currentAuctionInfo.setProductID(ProductDAO.create(currentAuctionInfo.getProduct(), currentAuctionInfo.getMinimumBid(), currentAuctionInfo.getMinimumIncrement()));

        JsonMessage msg = new JsonMessage(CallbackType.Client_OnAuctionStarted.name(), currentAuctionInfo, AuctionInfo.class.getName());
        sendJsonToAllClients(msg.toJson());
        sendChatMessageToClients(new ChatMessageResponseData("Eine Auktion wurde gestartet!", "SYSTEM", Helpers.getCurrentTime(), true, ""));
    }

    /**
     * Beendet die aktuelle Auktion, informiert alle Clients über das Ende und verarbeitet das Ergebnis der Auktion.
     * Wird aufgerufen, wenn die Auktionszeit abgelaufen ist oder die Auktion manuell beendet wird.
     */
    private synchronized void finishAuction(){
        String username = "";
        String bidInfo = " Kein Benutzer hat das Produkt ersteigert, da keine Gebote abgegeben wurden.";

        if (currentAuctionInfo.getBidData() != null){
            username = currentAuctionInfo.getBidData().client().getUsername();
            bidInfo = " Der Benutzer " + username + " hat das Produkt für " + Helpers.formatToEuro(currentAuctionInfo.getBidData().bid()) + " ersteigert.";
            ProductDAO.sell(currentAuctionInfo);
        }

        JsonMessage msg = new JsonMessage(CallbackType.Client_OnAuctionFinished.name());
        sendJsonToAllClients(msg.toJson());

        sendChatMessageToClients(new ChatMessageResponseData("Die Auktion ist abgeschlossen!" + bidInfo,
                "SYSTEM", Helpers.getCurrentTime(), true, ""));

        currentAuctionInfo = null;
    }

    /**
     * Verarbeitet eine Gebotsanfrage von einem Client.
     * Überprüft die Gültigkeit des Gebots und aktualisiert die Auktionsdaten entsprechend.
     *
     * @param context Der Kontext der Anfrage, enthält Informationen über die anfragende Verbindung und die Daten des Gebots.
     */
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
                    if (data.client().getUsername().equals(entry.getValue().getUsername())){
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
            sendChatMessageToClients(new ChatMessageResponseData(data.client().getUsername() + " hat ein Gebot abgegeben: " + Helpers.formatToEuro(data.bid()),
                    "SYSTEM", Helpers.getCurrentTime(), true, ""));

            JsonMessage bidMsg = new JsonMessage(CallbackType.Client_OnBid.name(), data, AuctionRoomBidData.class.getName());
            sendJsonToAllClients(bidMsg.toJson());
        }
    }

    /**
     * Sendet eine JSON-Nachricht an alle im Auktionsraum registrierten Clients.
     * Wird verwendet, um Zustandsaktualisierungen oder Benachrichtigungen zu versenden.
     *
     * @param json Die zu sendende JSON-Nachricht.
     */
    private synchronized void sendJsonToAllClients(String json){
        for (Map.Entry<WebSocket, ClientInfo> entry : registeredClients.entrySet()){
            entry.getKey().send(json);
        }
    }

    /**
     * Verarbeitet eine Anfrage, um Informationen über die aktuelle Auktion zu erhalten.
     * Sendet die aktuellen Auktionsinformationen als Antwort zurück an den anfragenden Client.
     *
     * @param context Der Kontext der Anfrage, enthält Informationen über die anfragende Verbindung.
     */
    private synchronized void onGetAuctionInfoRequest(CallbackContext context){
        JsonMessage msg = new JsonMessage(CallbackType.Client_Response.name(), currentAuctionInfo, AuctionInfo.class.getName());
        context.conn().send(msg.setResponseId(context.message().getMessageId()).toJson());
    }
}
