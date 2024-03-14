package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.function.Consumer;

public class ClientSocketManager extends WebSocketClient {
    private final HashMap<String, NetworkResponse> responses = new HashMap<>();
    private final HashMap<String, Consumer<JsonMessage>> callbacks = new HashMap<>();

    public void registerCallback(String callbackType, Consumer<JsonMessage> callback){
        callbacks.put(callbackType, callback);
    }

    public void unregisterCallback(String callbackType){
        callbacks.remove(callbackType);
    }

    public ClientSocketManager(URI serverUri) {
        super(serverUri);
    }

    public void send(JsonMessage message, NetworkResponse response){
        if (response != null){
            responses.put(message.getMessageId(), response);
        }

        super.send(message.toJson());
    }

    public void send(JsonMessage message){
        super.send(message.toJson());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Verbindung hergestellt");
    }

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

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Verbindung geschlossen");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Socket error");
        ex.printStackTrace();
    }

}
