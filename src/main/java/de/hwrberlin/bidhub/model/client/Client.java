package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.model.shared.IClient;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements IClient, Serializable {
    private String username;
    private String token;

    public Client(String username) throws RemoteException {
        super();

        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public String getToken(){
        return token;
    }

    @Override
    public void onAuthenticated(String token) throws RemoteException {
        this.token = token;
        System.out.println("Client authenticated!");
    }

    @Override
    public void onUnauthenticated() throws RemoteException {
        System.err.println("Client unauthenticated!");
        ClientApplication.logout();
    }

    @Override
    public void onHeartbeat() throws RemoteException {}
}
