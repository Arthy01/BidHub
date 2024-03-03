package de.hwrberlin.bidhub.model.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IAuctionRoomManagerService extends Remote {
    String createAuctionRoom(String title, String description, String password) throws RemoteException;
    ArrayList<AuctionRoomInfo> getAuctionRoomInfos() throws RemoteException;
}
