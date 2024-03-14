package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.AuctionRoomCreateRequestData;
import de.hwrberlin.bidhub.json.dataTypes.AuctionRoomCreateResponseData;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.WaitForResponse;

public class CreateAuctionHandler {
    public void createAuction(String title, String description, String password){
        AuctionRoomCreateRequestData data = new AuctionRoomCreateRequestData(title, description, Helpers.hashPassword(password));
        JsonMessage msg = new JsonMessage(CallbackType.Server_CreateAuctionRoom.name(), data, AuctionRoomCreateRequestData.class.getName());
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        AuctionRoomCreateResponseData responseData;

        try {
            responseData = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Callback auf dem Server nicht registriert!");
            throw new RuntimeException(e);
        }

        if (!responseData.success()){
            System.out.println("Der Raum konnte nicht erstellt werden!");
            return;
        }

        System.out.println("Auction Room erfolgreich erstellt!");
        JoinAuctionHandler.joinAuction(responseData.roomInfo().getId());
    }
}
