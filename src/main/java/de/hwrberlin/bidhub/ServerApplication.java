package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.model.server.AuctionRoomManagerService;
import de.hwrberlin.bidhub.model.server.LoginService;
import de.hwrberlin.bidhub.model.shared.ILoginService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApplication {
    public static final int TIMEOUT_TIME = 500;

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", RMIInfo.getHost());
        StartServices();
    }

    private static void StartServices() {
        try {
            Registry registry = LocateRegistry.createRegistry(RMIInfo.getPort());

            ILoginService loginService = new LoginService();
            registry.rebind("LoginService", loginService);

            AuctionRoomManagerService auctionRoomManagerService = new AuctionRoomManagerService(registry);
            registry.rebind("AuctionRoomManagerService", auctionRoomManagerService);

            System.out.println("Server started!");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
