package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.model.server.LoginHandler;
import de.hwrberlin.bidhub.model.shared.ILoginHandler;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApplication {

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", RMIInfo.getHost());
        StartServices();
    }

    private static void StartServices() {
        try {
            Registry registry = LocateRegistry.createRegistry(RMIInfo.getPort());
            ILoginHandler authenticator = new LoginHandler();

            registry.rebind("LoginService", authenticator);
            System.out.println("Server started!");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
