package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ServerSocketManager extends WebSocketServer {
    private final ExecutorService messageThreadPool;
    private final HashMap<String, Consumer<CallbackContext>> callbacks = new HashMap<>();

    public synchronized void registerCallback(String callbackType, Consumer<CallbackContext> callback){
        callbacks.put(callbackType, callback);
    }

    public synchronized void unregisterCallback(String callbackType){
        callbacks.remove(callbackType);
    }

    public ServerSocketManager(InetSocketAddress address) {
        super(address);
        this.setReuseAddr(true);
        messageThreadPool = Executors.newCachedThreadPool();
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
        //System.out.println("Nachricht von Client empfangen: " + message);

        JsonMessage msg = JsonMessage.fromJson(message);
        processMessage(new CallbackContext(msg, conn));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server gestartet!");
    }

    private synchronized void processMessage(CallbackContext context) {
       messageThreadPool.submit(() -> {
           Consumer<CallbackContext> callback;

           synchronized (callbacks){
               callback = callbacks.get(context.message().getCallbackType());
           }

           if (callback == null){
               System.out.println("Callback " + context.message().getCallbackType() + " nicht registriert! Message verworfen.");
               context.conn().send(new JsonMessage(CallbackType.Client_Response.name()).setResponseId(context.message().getMessageId()).toJson());
           }
           else {
               callback.accept(context);
           }
       });
    }
}
