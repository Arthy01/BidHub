package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.shared.ILoginHandler;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoginHandler extends UnicastRemoteObject implements ILoginHandler {

    public LoginHandler() throws RemoteException {
        super();
    }

    @Override
    public boolean onRequestLogin(String username, String passwordHash) throws RemoteException {
        if (!username.isBlank() && !passwordHash.isBlank()){
            System.out.println("Login information correct for user: " + username);
            return true;
        }

        System.out.println("Login information incorrect for user: " + username);
        return false;
    }


}
