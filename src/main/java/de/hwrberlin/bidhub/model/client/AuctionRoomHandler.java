package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.controller.InfoPopupController;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.*;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.Pair;
import de.hwrberlin.bidhub.util.StageManager;
import de.hwrberlin.bidhub.util.WaitForResponse;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * Verwaltet Auktionsraum-Interaktionen für einen Client.
 * Diese Klasse bietet Methoden zum Verwalten von Auktionsraumaktionen wie das Betreten und Verlassen eines Raums,
 * das Senden von Chat-Nachrichten, das Starten einer Auktion und das Platzieren von Geboten.
 */
public class AuctionRoomHandler {
    private final String roomId;

    /**
     * Erstellt einen neuen AuctionRoomHandler für einen spezifischen Auktionsraum.
     *
     * @param roomId Die ID des Auktionsraums, mit dem dieser Handler interagieren wird.
     */
    public AuctionRoomHandler(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Gibt die ID des Auktionsraums zurück, mit dem dieser Handler interagiert.
     *
     * @return Die ID des Auktionsraums.
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Überprüft, ob der aktuelle Benutzer der Initiator des Auktionsraums ist.
     * Sendet eine Anfrage an den Server und wartet auf die Antwort.
     *
     * @return true, wenn der Benutzer der Initiator ist, sonst false.
     * @throws RuntimeException Wenn der Callback auf dem Server nicht registriert ist.
     */
    public boolean getIsInitiator(){
        JsonMessage msg = new JsonMessage(CallbackType.Server_GetIsInitiator.name() + roomId);
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        SuccessResponseData successResponse;
        try {
            successResponse = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Callback auf dem Server nicht registriert!");
            throw new RuntimeException(e);
        }
        System.out.println("Initiator: " + successResponse.success());
        return successResponse.success();
    }

    /**
     * Ruft Informationen zum aktuellen Auktionsraum ab.
     * Diese Methode sendet eine Anfrage an den Server, um Informationen über den aktuellen Auktionsraum zu erhalten,
     * einschließlich der Liste der Teilnehmer und der Auktionsdetails. Bei Erfolg wird ein {@link AuctionRoomInfo}-Objekt zurückgegeben,
     * das diese Informationen enthält. Bei einem Fehler wird eine RuntimeException ausgelöst.
     *
     * @return Ein {@link AuctionRoomInfo}-Objekt mit den Details des Auktionsraums.
     * @throws RuntimeException Wenn der Callback auf dem Server nicht registriert ist oder ein Fehler beim Abrufen der Informationen auftritt.
     */
    public AuctionRoomInfo getAuctionRoomInfo(){
        JsonMessage msg = new JsonMessage(CallbackType.Server_GetAuctionRoomInfo.name() + roomId);
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        AuctionRoomInfo roomInfo;
        try {
            roomInfo = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Callback auf dem Server nicht registriert!");
            throw new RuntimeException(e);
        }

        return roomInfo;
    }

    /**
     * Ruft Informationen zur aktuellen Auktion im Auktionsraum ab.
     * Sendet eine Anfrage an den Server und wartet auf die Antwort, um die Auktionsdetails zu erhalten.
     *
     * @return Ein {@link AuctionInfo}-Objekt mit den Details der Auktion oder null, wenn keine Auktion aktiv ist.
     */
    public AuctionInfo getAuctionInfo(){
        JsonMessage msg = new JsonMessage(CallbackType.Server_GetAuctionInfoRequest.name() + roomId);
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        AuctionInfo auctionInfo;
        try {
            auctionInfo = response.getResponse().getData();
            return auctionInfo;
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren der Auction Info! Möglicherweise ist keine Auktion gestartet, dann ist das Verhalten korrekt.");
            return null;
        }
    }

    /**
     * Verlässt den aktuellen Auktionsraum und kehrt zum Dashboard zurück.
     * Zeigt je nach Verlassensgrund eine entsprechende Nachricht in einem Popup an.
     *
     * @param reason Der Grund für das Verlassen des Auktionsraums.
     */
    public void leaveRoom(LeaveRoomReason reason){
        ClientApplication.unregisterFromCurrentConnectedRoom();
        StageManager.setScene(FxmlFile.Dashboard, true);
        System.out.println("Auction Room " + roomId + " wurde verlassen! (Grund: " + reason.name() + ")");

        if (reason == LeaveRoomReason.Self)
            return;

        Pair<InfoPopupController, Stage> popup = StageManager.createPopup(FxmlFile.InfoPopup, "Raum verlassen");

        if (reason == LeaveRoomReason.Kick){
            popup.getKey().initialize("Du wurdest aus dem Auktionsraum gekicked!", popup.getValue());
        }
        else if (reason == LeaveRoomReason.Ban){
            popup.getKey().initialize("Du wurdest aus dem Auktionsraum gebannt! Du kannst ihn nicht wieder betreten.", popup.getValue());
        }
        else if (reason == LeaveRoomReason.Closed){
            popup.getKey().initialize("Der Auktionsraum wurde vom Auktionär geschlossen.", popup.getValue());
        }
        else if (reason == LeaveRoomReason.Unspecified) {
            popup.getKey().initialize("Der Auktionsraum wurde aus einem unbekannten Grund geschlossen.", popup.getValue());
        }
    }

    /**
     * Sendet eine Chat-Nachricht im Auktionsraum oder führt einen Chat-Befehl aus, wenn die Nachricht mit "/" beginnt.
     *
     * @param message Die zu sendende Nachricht oder der auszuführende Befehl.
     * @return Eine leere Zeichenkette oder eine Fehlermeldung, wenn der Befehl nicht existiert.
     */
    public String sendChatMessage(String message){
        if (message.startsWith("/")){
            return handleChatCommand(message);
        }

        System.out.println("Sende Chat Nachricht im Auction Room " + roomId + ".");
        JsonMessage msg = new JsonMessage(CallbackType.Server_ReceiveChatMessageFromClient.name() + roomId,
                new ChatMessageRequestData(message, ""),
                ChatMessageRequestData.class.getName());

        ClientApplication.getSocketManager().send(msg);
        return "";
    }

    /**
     * Verarbeitet einen Chat-Befehl, der mit "/" beginnt.
     * Unterstützt Befehle wie /pm für private Nachrichten, /kick zum Entfernen und /ban zum Verbannen von Benutzern.
     *
     * @param message Die Chat-Nachricht, die den Befehl enthält.
     * @return Eine Bestätigung oder Fehlermeldung, abhängig vom Ergebnis der Befehlsausführung.
     */
    private String handleChatCommand(String message){
        String[] parts = message.split(" ", 2);
        String command = parts[0];

        return switch (command.toLowerCase()) {
            case "/pm" -> handlePrivateMessageCommand(message);
            case "/kick" -> handleKickClient(message);
            case "/ban" -> handleBanClient(message);
            default -> "Der Befehl " + command + " existiert nicht!";
        };
    }

    /**
     * Verarbeitet den Befehl für das Senden einer privaten Nachricht an einen anderen Benutzer im Auktionsraum.
     *
     * @param rawMessage Die gesamte Nachricht, die den Befehl und die Parameter enthält.
     * @return Eine leere Zeichenkette oder eine Fehlermeldung, wenn die Parameter ungültig sind.
     */
    private String handlePrivateMessageCommand(String rawMessage){
        String recipient;
        String message;
        try{
            String[] parts = rawMessage.split(" ", 3);
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            recipient = args[0];
            message = args[1];
        }
        catch (Exception e){
            return "Ungültige Parameterreihenfolge für den Befehl /pm. (/pm {benutzername} {nachricht}";
        }

        if (recipient.equals(ClientApplication.getApplicationClient().getUsername()))
            return "Du kannst keine private Nachricht an dich selbst schreiben!";

        AuctionRoomInfo info = getAuctionRoomInfo();

        if (!info.getClients().containsKey(recipient))
            return "Der Benutzer " + recipient + " ist nicht im Auktionsraum!";

        System.out.println("Sende private Nachricht im Auction Room " + roomId + " an Client " + recipient + ".");

        JsonMessage msg = new JsonMessage(CallbackType.Server_ReceiveChatMessageFromClient.name() + roomId,
                new ChatMessageRequestData(message, recipient),
                ChatMessageRequestData.class.getName());

        ClientApplication.getSocketManager().send(msg);
        return "";
    }

    /**
     * Verarbeitet den Befehl zum Entfernen (Kicken) eines Benutzers aus dem Auktionsraum.
     * Nur der Initiator des Raums kann diesen Befehl ausführen.
     *
     * @param message Die Nachricht, die den Befehl und den Benutzernamen des zu entfernenden Benutzers enthält.
     * @return Eine leere Zeichenkette oder eine Fehlermeldung, wenn der Benutzer nicht berechtigt ist oder der Zielbenutzer nicht gefunden wurde.
     */
    private String handleKickClient(String message){
        if (!getIsInitiator())
            return "Du bist nicht berechtigt einen Benutzer zu kicken!";

        String kickUsername;
        try{
            String[] parts = message.split(" ", 2);
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            kickUsername = args[0];
        }
        catch (Exception e){
            return "Ungültige Parameterreihenfolge für den Befehl /kick. (/kick {benutzername}";
        }

        if (kickUsername.equals(ClientApplication.getApplicationClient().getUsername())) {
            return "Du kannst dich nicht selbst kicken!";
        }

        AuctionRoomInfo info = getAuctionRoomInfo();

        if (!info.getClients().containsKey(kickUsername))
            return "Der Benutzer " + kickUsername + " ist nicht im Auktionsraum!";

        System.out.println("Versuche den Benutzer " + kickUsername + " zu kicken.");

        JsonMessage msg = new JsonMessage(CallbackType.Server_AuctionRoomKickClient.name() + roomId,
                new KickBanRequestData(kickUsername),
                KickBanRequestData.class.getName());

        ClientApplication.getSocketManager().send(msg);
        return "";
    }

    /**
     * Verarbeitet den Befehl zum Verbannen eines Benutzers aus dem Auktionsraum.
     * Nur der Initiator des Raums kann diesen Befehl ausführen.
     *
     * @param message Die Nachricht, die den Befehl und den Benutzernamen des zu verbannenden Benutzers enthält.
     * @return Eine leere Zeichenkette oder eine Fehlermeldung, wenn der Benutzer nicht berechtigt ist oder der Zielbenutzer nicht gefunden wurde.
     */
    private String handleBanClient(String message){
        if (!getIsInitiator())
            return "Du bist nicht berechtigt einen Benutzer zu bannen!";

        String banUsername;
        try{
            String[] parts = message.split(" ", 2);
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            banUsername = args[0];
        }
        catch (Exception e){
            return "Ungültige Parameterreihenfolge für den Befehl /ban. (/ban {benutzername}";
        }

        if (banUsername.equals(ClientApplication.getApplicationClient().getUsername())) {
            return "Du kannst dich nicht selbst bannen!";
        }

        AuctionRoomInfo info = getAuctionRoomInfo();

        if (!info.getClients().containsKey(banUsername))
            return "Der Benutzer " + banUsername + " ist nicht im Auktionsraum!";

        System.out.println("Versuche den Benutzer " + banUsername + " zu bannen.");

        JsonMessage msg = new JsonMessage(CallbackType.Server_AuctionRoomBanClient.name() + roomId,
                new KickBanRequestData(banUsername),
                KickBanRequestData.class.getName());

        ClientApplication.getSocketManager().send(msg);
        return "";
    }

    /**
     * Startet eine Auktion im Auktionsraum.
     * Sendet eine Anfrage an den Server, um die Auktion mit den angegebenen Details zu starten.
     *
     * @param auctionInfo Die Informationen zur zu startenden Auktion.
     * @throws RuntimeException Wenn ein Fehler beim Konvertieren der Antwort auftritt.
     */
    public void startAuction(AuctionInfo auctionInfo){
        JsonMessage msg = new JsonMessage(CallbackType.Server_AuctionRoomStartAuction.name() + roomId, auctionInfo, AuctionInfo.class.getName());
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        SuccessResponseData data;
        try {
            data = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren!");
            throw new RuntimeException(e);
        }

        System.out.println("Konnte Auktion gestartet werden: " + data.success());
    }

    /**
     * Platziert ein Gebot in der aktuellen Auktion des Auktionsraums.
     * Sendet eine Anfrage an den Server, um das Gebot zu platzieren, und wartet auf die Antwort.
     *
     * @param bid Der Betrag des Gebots.
     * @return true, wenn das Gebot erfolgreich platziert wurde, sonst false.
     * @throws RuntimeException Wenn ein Fehler beim Konvertieren der Antwort auftritt.
     */
    public boolean placeBid(float bid){
        JsonMessage msg = new JsonMessage(CallbackType.Server_AuctionRoomOnBidRequest.name() + roomId, new AuctionRoomBidData(bid, ClientApplication.getApplicationClient()), AuctionRoomBidData.class.getName());
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        SuccessResponseData data;
        try {
            data = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren der Bid Response!");
            throw new RuntimeException(e);
        }

        return data.success();
    }
}
