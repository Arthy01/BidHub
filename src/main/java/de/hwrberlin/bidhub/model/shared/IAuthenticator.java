package de.hwrberlin.bidhub.model.shared;

import de.hwrberlin.bidhub.model.client.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuthenticator extends Remote {
    boolean authenticate(LoginInfo info) throws RemoteException;
}
