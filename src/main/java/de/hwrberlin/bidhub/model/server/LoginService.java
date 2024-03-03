package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.shared.ILoginService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoginService extends UnicastRemoteObject implements ILoginService {

    public LoginService() throws RemoteException {
        super();
    }

    @Override
    public boolean requestLogin(String username, String passwordHash) throws RemoteException {
        if (!username.isBlank() && !passwordHash.isBlank()){
            System.out.println("Login information correct for user: " + username);
            return true;
        }

        System.out.println("Login information incorrect for user: " + username);
        return false;
    }


}
