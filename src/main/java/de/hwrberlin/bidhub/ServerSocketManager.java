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

/**
 * Der ServerSocketManager verwaltet die WebSocket-Verbindung zum Server und handhabt eingehende
 * Nachrichten sowie Callback-Registrierungen.
 */
public class ServerSocketManager extends WebSocketServer {
    private final ExecutorService messageThreadPool;
    private final HashMap<String, Consumer<CallbackContext>> callbacks = new HashMap<>();

    /**
     * Registriert einen Callback für einen bestimmten Callback-Typ.
     *
     * @param callbackType der Typ des Callbacks
     * @param callback der Callback, der registriert werden soll
     */
    public synchronized void registerCallback(String callbackType, Consumer<CallbackContext> callback){
        callbacks.put(callbackType, callback);
    }

    /**
     * Entfernt einen zuvor registrierten Callback.
     *
     * @param callbackType der Typ des Callbacks, der entfernt werden soll
     */
    public synchronized void unregisterCallback(String callbackType){
        callbacks.remove(callbackType);
    }

    /**
     * Konstruktor für den ServerSocketManager.
     *
     * @param address die Adresse, an die der Server gebunden werden soll
     */
    public ServerSocketManager(InetSocketAddress address) {
        super(address);
        this.setReuseAddr(true);
        messageThreadPool = Executors.newCachedThreadPool();
    }

    /**
     * Wird aufgerufen, wenn eine neue WebSocket-Verbindung hergestellt wird.
     *
     * @param conn die WebSocket-Verbindung
     * @param handshake die Handshake-Daten
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Verbindung hergestellt: " + conn.getRemoteSocketAddress());
    }

    /**
     * Wird aufgerufen, wenn eine WebSocket-Verbindung geschlossen wird.
     *
     * @param conn die WebSocket-Verbindung
     * @param code der Schließungscode
     * @param reason der Grund für die Schließung
     * @param remote ob die Schließung von der entfernten Seite initiiert wurde
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Verbindung geschlossen: " + conn.getRemoteSocketAddress());
    }

    /**
     * Wird aufgerufen, wenn eine Nachricht von einem Client empfangen wird.
     *
     * @param conn die WebSocket-Verbindung
     * @param message die empfangene Nachricht
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        //System.out.println("Nachricht von Client empfangen: " + message);

        JsonMessage msg = JsonMessage.fromJson(message);
        processMessage(new CallbackContext(msg, conn));
    }

    /**
     * Wird aufgerufen, wenn ein Fehler in der WebSocket-Verbindung auftritt.
     *
     * @param conn die WebSocket-Verbindung
     * @param ex die aufgetretene Ausnahme
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    /**
     * Wird aufgerufen, wenn der Server gestartet wird.
     */
    @Override
    public void onStart() {
        System.out.println("Server gestartet!");
    }

    /**
     * Verarbeitet eine empfangene Nachricht in einem separaten Thread.
     *
     * @param context der Kontext der empfangenen Nachricht
     */
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
