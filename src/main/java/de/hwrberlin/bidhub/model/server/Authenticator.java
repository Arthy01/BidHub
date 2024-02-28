package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.client.Client;
import de.hwrberlin.bidhub.model.shared.IAuthenticator;
import de.hwrberlin.bidhub.model.shared.IClient;
import de.hwrberlin.bidhub.model.shared.LoginInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class Authenticator extends UnicastRemoteObject implements IAuthenticator {
    public Authenticator() throws RemoteException {
        super();
    }

    @Override
    public boolean authenticate(LoginInfo info) throws RemoteException {
        System.out.println("Get request");
        if (!info.getUsername().isBlank() && !info.getPassword().isBlank()){
            System.out.println("Request successful!");
            System.out.println("Client: " + info.getClient());
            //info.getClient().onAuthenticated(UUID.randomUUID().toString());
            return true;
        }
        System.out.println("Request failed!");
        return false;
    }

    @Override
    public boolean debug_ping(String user) throws RemoteException{
        System.out.println("Ping for: " + user);
        return true;
    }


}
