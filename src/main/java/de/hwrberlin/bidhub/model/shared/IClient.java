package de.hwrberlin.bidhub.model.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
    void onAuthenticated(String token) throws RemoteException;

    void onUnauthenticated() throws RemoteException;

    void onHeartbeat() throws RemoteException;
}
