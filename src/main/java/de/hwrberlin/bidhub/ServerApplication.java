package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.model.server.AuctionRoomManagerService;
import de.hwrberlin.bidhub.model.server.LoginService;

import java.net.InetSocketAddress;

public class ServerApplication {
    private static ServerSocketManager serverSocketManager;
    public static void main(String[] args) {
        serverSocketManager = new ServerSocketManager(new InetSocketAddress(SocketInfo.getHost(), SocketInfo.getPort()));
        serverSocketManager.start();

        LoginService loginService = new LoginService();
        AuctionRoomManagerService auctionRoomManagerService = new AuctionRoomManagerService();
    }

    public static ServerSocketManager getSocketManager(){
        return serverSocketManager;
    }
}
