package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.model.server.*;

import java.net.InetSocketAddress;
import java.sql.SQLException;

/**
 * Die ServerApplication ist der Haupteinstiegspunkt für die Server-Anwendung. Sie verwaltet
 * die Verbindung zur Datenbank und startet den ServerSocketManager.
 */
public class ServerApplication {
    private static ServerSocketManager serverSocketManager;

    /**
     * Hauptmethode der Anwendung. Initialisiert die Verbindung zur Datenbank und startet den ServerSocketManager.
     *
     * @param args die Befehlszeilenargumente
     */
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

    /**
     * Gibt den ServerSocketManager zurück.
     *
     * @return der ServerSocketManager
     */
    public static ServerSocketManager getSocketManager(){
        return serverSocketManager;
    }
}
