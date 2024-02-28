package de.hwrberlin.bidhub.model.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILoginHandler extends Remote {
    boolean onRequestLogin(String username, String passwordHash) throws RemoteException;
}
