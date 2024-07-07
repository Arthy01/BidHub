package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Der ClientSocketManager verwaltet die WebSocket-Verbindung zum Server und handhabt eingehende
 * und ausgehende Nachrichten sowie Callback-Registrierungen.
 */
public class ClientSocketManager extends WebSocketClient {
    private final HashMap<String, NetworkResponse> responses = new HashMap<>();
    private final HashMap<String, Consumer<JsonMessage>> callbacks = new HashMap<>();

    /**
     * Registriert einen Callback für einen bestimmten Callback-Typ.
     *
     * @param callbackType der Typ des Callbacks
     * @param callback der Callback, der registriert werden soll
     */
    public void registerCallback(String callbackType, Consumer<JsonMessage> callback){
        callbacks.put(callbackType, callback);
    }

    /**
     * Entfernt einen zuvor registrierten Callback.
     *
     * @param callbackType der Typ des Callbacks, der entfernt werden soll
     */
    public void unregisterCallback(String callbackType){
        callbacks.remove(callbackType);
    }

    /**
     * Konstruktor für den ClientSocketManager.
     *
     * @param serverUri die URI des Servers, mit dem die Verbindung hergestellt werden soll
     */
    public ClientSocketManager(URI serverUri) {
        super(serverUri);
    }

    /**
     * Sendet eine JsonMessage und erwartet eine NetworkResponse.
     *
     * @param message die zu sendende Nachricht
     * @param response die erwartete Antwort
     */
    public void send(JsonMessage message, NetworkResponse response){
        if (response != null){
            responses.put(message.getMessageId(), response);
        }

        super.send(message.toJson());
    }

    /**
     * Sendet eine JsonMessage, ohne eine NetworkResponse zu erwarten
     *
     * @param message die zu sendende Nachricht
     */
    public void send(JsonMessage message){
        super.send(message.toJson());
    }

    /**
     * Wird aufgerufen, wenn die Verbindung zum Server hergestellt wird.
     *
     * @param handshakedata die Handshake-Daten vom Server
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Verbindung hergestellt");
    }

    /**
     * Wird aufgerufen, wenn eine Nachricht vom Server empfangen wird.
     *
     * @param message die empfangene Nachricht
     */
    @Override
    public void onMessage(String message) {
        System.out.println("Nachricht vom Server: " + message);

        JsonMessage msg = JsonMessage.fromJson(message);

        if (msg.getCallbackType().equals(CallbackType.Client_Response.name())){
            NetworkResponse response = responses.get(msg.getResponseId());

            if (response == null) {
                System.out.println("Response " + msg.getMessageId() + " wurde nicht gefunden! Message verworfen!");
            }
            else{
                responses.remove(msg.getResponseId());
                response.setResponse(msg);
            }
        }
        else{
            Consumer<JsonMessage> callback = callbacks.get(msg.getCallbackType());

            if (callback == null){
                System.out.println("Callback " + msg.getCallbackType() + " wurde nicht gefunden! Message verworfen!");
            }
            else{
                callback.accept(msg);
            }
        }
    }

    /**
     * Wird aufgerufen, wenn die Verbindung zum Server geschlossen wird.
     *
     * @param code der Schließungscode
     * @param reason der Grund für die Schließung
     * @param remote ob die Schließung von einer entfernten Instanz initiiert wurde
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Verbindung geschlossen");
    }

    /**
     * Wird aufgerufen, wenn ein Fehler in der WebSocket-Verbindung auftritt.
     *
     * @param ex die aufgetretene Ausnahme
     */
    @Override
    public void onError(Exception ex) {
        System.out.println("Socket error");
        ex.printStackTrace();
    }

}
