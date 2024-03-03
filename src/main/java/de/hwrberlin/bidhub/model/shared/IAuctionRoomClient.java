package de.hwrberlin.bidhub.model.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuctionRoomClient extends Remote {
    void receiveMessage(String message, boolean important) throws RemoteException;
    void kick(String kickMessage) throws RemoteException;
    void clientStateChange() throws RemoteException;
}
