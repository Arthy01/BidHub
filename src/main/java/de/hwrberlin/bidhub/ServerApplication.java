package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.model.server.Authenticator;
import de.hwrberlin.bidhub.model.shared.IAuthenticator;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApplication {

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", "212.227.233.17");
        StartServices();
    }

    private static void StartServices() {
        try {
            Registry registry = LocateRegistry.createRegistry(RMIInfo.getPort());
            IAuthenticator authenticator = new Authenticator();

            registry.rebind("AuthenticationService", authenticator);
            System.out.println("Server started!");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
