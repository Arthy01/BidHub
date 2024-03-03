package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.RMIInfo;
import de.hwrberlin.bidhub.controller.AuctionRoomController;
import de.hwrberlin.bidhub.exceptions.InvalidInputException;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomManagerService;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomService;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.StageManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CreateAuctionHandler {
    private boolean isPrivate = false;
    private IAuctionRoomManagerService auctionRoomManagerService;

    public CreateAuctionHandler() throws RemoteException, NotBoundException{
        locateServices();
    }

    public void changeVisibility(boolean isPrivate){
        this.isPrivate = isPrivate;
    }

    private void locateServices() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(RMIInfo.getHost(), RMIInfo.getPort());
        auctionRoomManagerService = (IAuctionRoomManagerService) registry.lookup("AuctionRoomManagerService");
    }

    public void createAuction(String title, String description, String password) throws InvalidInputException {
        String hashedPassword = "";

        if ((password.isBlank() && isPrivate) || password.startsWith(" ")) {
            throw new InvalidInputException("Password is blank or has leading spaces!");
        }

        if (isPrivate)
            hashedPassword = Helpers.hashPassword(password);

        try {
            String roomId = auctionRoomManagerService.createAuctionRoom(title, description, hashedPassword);

            Registry registry = LocateRegistry.getRegistry(RMIInfo.getHost(), RMIInfo.getPort());
            IAuctionRoomService auctionRoomService = (IAuctionRoomService) registry.lookup(roomId);

            AuctionRoomController controller = StageManager.setScene(FxmlFile.AuctionRoom, true);
            controller.initialize(auctionRoomService, true);
        }
        catch (RemoteException | NotBoundException e) {
            ClientApplication.logout();
            e.printStackTrace();
        }
    }
}
