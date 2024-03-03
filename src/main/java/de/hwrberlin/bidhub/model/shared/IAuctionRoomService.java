package de.hwrberlin.bidhub.model.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuctionRoomService extends Remote {
    void sendMessage(IAuctionRoomClient sender, String message) throws RemoteException;
    void closeRoom() throws RemoteException;
    void registerClient(IAuctionRoomClient client, AuctionRoomClientData clientData) throws RemoteException;
    void unregisterClient(IAuctionRoomClient client, AuctionRoomClientData clientData) throws RemoteException;
    boolean comparePassword(String password) throws RemoteException;
    AuctionRoomInfo getRoomInfo() throws RemoteException;
}
