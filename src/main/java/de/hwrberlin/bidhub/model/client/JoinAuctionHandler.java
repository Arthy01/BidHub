package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.controller.AuctionRoomController;
import de.hwrberlin.bidhub.controller.InfoPopupController;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.AuctionRoomPasswordValidationRequestData;
import de.hwrberlin.bidhub.json.dataTypes.AuctionRoomRegisterClientRequestData;
import de.hwrberlin.bidhub.json.dataTypes.AvailableAuctionRoomsResponse;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.model.server.ClientInfo;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class JoinAuctionHandler {
    public static void joinAuction(String roomId){
        NetworkResponse response = new NetworkResponse();
        JsonMessage msg = new JsonMessage(
                CallbackType.Server_RegisterClient.name() + roomId,
                new AuctionRoomRegisterClientRequestData(ClientApplication.getApplicationClient()),
                AuctionRoomRegisterClientRequestData.class.getName());

        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        SuccessResponseData data;
        try {
            data = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Callback auf dem Server nicht registriert!");
            throw new RuntimeException(e);
        }

        if (!data.success()){
            Pair<InfoPopupController, Stage> popup = StageManager.createPopup(FxmlFile.InfoPopup, "Du wurdest gebannt");
            popup.getKey().initialize("Du kannst dem Raum nicht beitreten da du gebannt wurdest!", popup.getValue());
            return;
        }

        ClientApplication.getApplicationClient().setCurrentConnectedRoom(roomId);
        AuctionRoomController controller = StageManager.setScene(FxmlFile.AuctionRoom, true);
        controller.initialize(roomId);
        System.out.println("Auction Room erfolgreich beigetreten!");
    }

    public ArrayList<AuctionRoomInfo> getAvailableRooms(){
        NetworkResponse response = new NetworkResponse();
        JsonMessage msg = new JsonMessage(CallbackType.Server_GetAvailableRooms.name());
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        AvailableAuctionRoomsResponse data;
        try {
            data = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }

        return data.infos();
    }

    public boolean validateRoomPassword(String roomId, String password){
        AuctionRoomPasswordValidationRequestData data = new AuctionRoomPasswordValidationRequestData(Helpers.hashPassword(password));
        NetworkResponse response = new NetworkResponse();
        JsonMessage msg = new JsonMessage(CallbackType.Server_ValidateRoomPassword.name() + roomId, data, AuctionRoomPasswordValidationRequestData.class.getName());
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        SuccessResponseData successResponseData;
        try {
            successResponseData = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("This room does not exist!");
            return false;
        }

        return successResponseData.success();
    }
}
