package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import org.java_websocket.WebSocket;

public class ClientInfo {
    private final WebSocket connection;
    private final ApplicationClient client;
    private final boolean isInitiator;

    public ClientInfo(WebSocket connection, ApplicationClient client, boolean isInitiator) {
        this.connection = connection;
        this.client = client;
        this.isInitiator = isInitiator;
    }

    public WebSocket getConnection() {
        return connection;
    }

    public String getUsername() {
        return client.getUsername();
    }
    public long getId(){return client.getId();}

    public boolean isInitiator() {
        return isInitiator;
    }
}
