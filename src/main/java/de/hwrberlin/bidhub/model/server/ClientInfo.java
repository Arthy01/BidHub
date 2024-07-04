package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import org.java_websocket.WebSocket;

/**
 * Repräsentiert die Informationen eines Clients innerhalb des Servers.
 * Diese Klasse speichert die Verbindungsdetails, die Client-Informationen und ob der Client der Initiator einer Aktion ist.
 */
public class ClientInfo {
    private final WebSocket connection;
    private final ApplicationClient client;
    private final boolean isInitiator;

    /**
     * Erstellt eine neue Instanz von ClientInfo.
     *
     * @param connection Die WebSocket-Verbindung des Clients.
     * @param client Die Client-Informationen, einschließlich Benutzername und ID.
     * @param isInitiator Gibt an, ob der Client der Initiator einer Aktion ist.
     */
    public ClientInfo(WebSocket connection, ApplicationClient client, boolean isInitiator) {
        this.connection = connection;
        this.client = client;
        this.isInitiator = isInitiator;
    }

    /**
     * Gibt die WebSocket-Verbindung des Clients zurück.
     *
     * @return Die WebSocket-Verbindung des Clients.
     */
    public WebSocket getConnection() {
        return connection;
    }

    /**
     * Gibt den Benutzernamen des Clients zurück.
     *
     * @return Der Benutzername des Clients.
     */
    public String getUsername() {
        return client.getUsername();
    }

    /**
     * Gibt die eindeutige ID des Clients zurück.
     *
     * @return Die ID des Clients als long.
     */
    public long getId(){return client.getId();}

    /**
     * Überprüft, ob der Client der Initiator einer Aktion ist.
     *
     * @return true, wenn der Client der Initiator ist, sonst false.
     */
    public boolean isInitiator() {
        return isInitiator;
    }
}
