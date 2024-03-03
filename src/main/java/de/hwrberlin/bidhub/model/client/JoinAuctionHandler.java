package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.RMIInfo;
import de.hwrberlin.bidhub.controller.AuctionRoomController;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomManagerService;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomService;
import de.hwrberlin.bidhub.model.shared.ILoginService;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.FxmlRef;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class JoinAuctionHandler {
    private IAuctionRoomManagerService managerService;
    public JoinAuctionHandler() throws RemoteException, NotBoundException{
        locateServices();
    }

    private void locateServices() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(RMIInfo.getHost(), RMIInfo.getPort());
        managerService = (IAuctionRoomManagerService) registry.lookup("AuctionRoomManagerService");
    }

    public ArrayList<AuctionRoomInfo> getPreviews() throws RemoteException{
        return managerService.getAuctionRoomInfos();
    }

    public void joinAuction(String roomId, String password) {
        try{
            if (!roomId.startsWith("ARM_"))
                roomId = "ARM_" + roomId;

            Registry registry = LocateRegistry.getRegistry(RMIInfo.getHost(), RMIInfo.getPort());
            IAuctionRoomService auctionRoomService = (IAuctionRoomService) registry.lookup(roomId);

            if (!password.isEmpty())
                password = Helpers.hashPassword(password);

            boolean correctPassword = auctionRoomService.comparePassword(password);

            if (!correctPassword){
                System.out.println("Falsches Passwort!");
                return;
            }

            AuctionRoomController controller = StageManager.setScene(FxmlFile.AuctionRoom, true);
            controller.initialize(auctionRoomService, false);
        }
        catch (NotBoundException e){
            System.out.println("Die Auktion existiert nicht!");
        }
        catch (RemoteException e) {
            ClientApplication.logout();
            e.printStackTrace();
        }
    }
}
