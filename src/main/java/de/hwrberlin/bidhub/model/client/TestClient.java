package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.model.shared.IClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TestClient extends UnicastRemoteObject implements IClient {
    public TestClient() throws RemoteException {
        super();
    }

    @Override
    public void onAuthenticated(String token) throws RemoteException {

    }

    @Override
    public void onUnauthenticated() throws RemoteException {

    }

    @Override
    public void onHeartbeat() throws RemoteException {

    }
}
