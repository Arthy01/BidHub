package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.ChatMessageRequestData;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.StageManager;
import de.hwrberlin.bidhub.util.WaitForResponse;

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

    public void leaveRoom(){
        ClientApplication.unregisterFromCurrentConnectedRoom();
        StageManager.setScene(FxmlFile.Dashboard, true);
        System.out.println("Auction Room " + roomId + " wurde verlassen!");
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

        switch (command.toLowerCase()) {
            case "/pm":
                return handlePrivateMessageCommand(message);
                /*
            case "/kick":
                handleKickUser(args, sender);
                break;
            case "/ban":
                handleBanUser(args, sender);
                break;
            */
            default:
                return "Der Befehl " + command + " existiert nicht!";
        }
    }

    private String handlePrivateMessageCommand(String rawMessage){
        String recipient;
        String message;
        try{
            String[] parts = rawMessage.split(" ", 3);
            String command = parts[0];
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
}
