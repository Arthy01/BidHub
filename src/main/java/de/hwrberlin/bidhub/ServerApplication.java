package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.model.server.*;

import java.net.InetSocketAddress;
import java.sql.SQLException;

public class ServerApplication {
    private static ServerSocketManager serverSocketManager;
    public static void main(String[] args) {
        serverSocketManager = new ServerSocketManager(new InetSocketAddress(SocketInfo.getHost(), SocketInfo.getPort()));
        serverSocketManager.start();

        try {
            SQL.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LoginService loginService = new LoginService();
        AuctionRoomManagerService auctionRoomManagerService = new AuctionRoomManagerService();
        UserInformationService userInformationService = new UserInformationService();
        TransactionInformationService transactionInformationService = new TransactionInformationService();
    }

    public static ServerSocketManager getSocketManager(){
        return serverSocketManager;
    }
}
