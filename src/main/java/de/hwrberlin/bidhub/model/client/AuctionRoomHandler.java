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

public class AuctionRoomHandler {
    private final String roomId;

    public AuctionRoomHandler(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

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

    public boolean placeBid(float bid){
        JsonMessage msg = new JsonMessage(CallbackType.Server_AuctionRoomOnBidRequest.name() + roomId, new AuctionRoomBidData(bid, ClientApplication.getApplicationClient().getUsername()), AuctionRoomBidData.class.getName());
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
