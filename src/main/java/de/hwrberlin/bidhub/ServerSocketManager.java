package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.function.Consumer;

public class ServerSocketManager extends WebSocketServer {
    private final HashMap<CallbackType, Consumer<CallbackContext>> callbacks = new HashMap<>();

    public void registerCallback(CallbackType callbackType, Consumer<CallbackContext> callback){
        callbacks.put(callbackType, callback);
    }

    public void unregisterCallback(CallbackType callbackType){
        callbacks.remove(callbackType);
    }

    public ServerSocketManager(InetSocketAddress address) {
        super(address);
        this.setReuseAddr(true);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Verbindung hergestellt: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Verbindung geschlossen: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Nachricht von Client empfangen: " + message);
        //conn.send(message);
        JsonMessage msg = JsonMessage.fromJson(message);
        callbacks.get(msg.getCallbackType()).accept(new CallbackContext(msg, conn));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server gestartet!");
    }
}
