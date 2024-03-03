package de.hwrberlin.bidhub.model.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILoginService extends Remote {
    boolean requestLogin(String username, String passwordHash) throws RemoteException;
}
