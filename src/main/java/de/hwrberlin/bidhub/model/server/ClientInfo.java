package de.hwrberlin.bidhub.model.server;

import org.java_websocket.WebSocket;

public class ClientInfo {
    private WebSocket connection;
    private String username;
    private boolean isInitiator;

    public ClientInfo(WebSocket connection, String username, boolean isInitiator) {
        this.connection = connection;
        this.username = username;
        this.isInitiator = isInitiator;
    }

    public WebSocket getConnection() {
        return connection;
    }

    public void setConnection(WebSocket connection) {
        this.connection = connection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isInitiator() {
        return isInitiator;
    }

    public void setInitiator(boolean initiator) {
        isInitiator = initiator;
    }
}
