package de.hwrberlin.bidhub.json;

import com.google.gson.Gson;
import de.hwrberlin.bidhub.model.shared.CallbackType;

import java.util.UUID;

/**
 * Die Klasse JsonMessage stellt eine Nachricht dar, die in JSON-Format serialisiert und
 * deserialisiert werden kann. Sie wird verwendet, um Daten zwischen Client und Server auszutauschen.
 */
public class JsonMessage {
    private static final Gson gson = new Gson();

    private final String messageId;
    private String responseId;
    private final String callbackType;
    private final String data;
    private final String dataType;


    /**
     * Konstruktor für JsonMessage ausschließlich mit dem Callback-Typ.
     *
     * @param callbackType der Typ des Callbacks
     */
    public JsonMessage(String callbackType){
        messageId = UUID.randomUUID().toString();
        this.callbackType = callbackType;

        responseId = "";
        data = "";
        dataType = "";
    }

    /**
     * Konstruktor für JsonMessage mit Callback-Typ, Daten und Datentyp.
     *
     * @param callbackType der Typ des Callbacks
     * @param data die Daten der Nachricht
     * @param dataType der Typ der Daten
     */
    public JsonMessage(String callbackType, Object data, String dataType){
        messageId = UUID.randomUUID().toString();
        this.callbackType = callbackType;

        responseId = "";
        this.data = gson.toJson(data);
        this.dataType = dataType;
    }

    /**
     * Gibt die ID der Nachricht zurück.
     *
     * @return die Nachrichten-ID
     */
    public String getMessageId(){
        return messageId;
    }

    /**
     * Gibt den Callback-Typ der Nachricht zurück.
     *
     * @return der Callback-Typ
     */
    public String getCallbackType(){
        return callbackType;
    }

    /**
     * Gibt die Daten der Nachricht zurück und konvertiert sie in den angegebenen Typ.
     *
     * @param <T> der Typ der zurückgegebenen Daten
     * @return die Daten der Nachricht
     * @throws Exception wenn die Konvertierung fehlschlägt
     */
    public <T> T getData() throws Exception {
        System.out.println("Konvertierung in: " + dataType);
        return (T) gson.fromJson(data, Class.forName(dataType));
    }

    /**
     * Gibt die Antwort-ID der Nachricht zurück.
     *
     * @return die Antwort-ID
     */
    public String getResponseId() {
        return responseId;
    }

    /**
     * Setzt die Antwort-ID der Nachricht.
     *
     * @param responseId die Antwort-ID
     * @return die aktualisierte JsonMessage-Instanz
     */
    public JsonMessage setResponseId(String responseId) {
        this.responseId = responseId;
        return this;
    }

    /**
     * Serialisiert die Nachricht in JSON-Format.
     *
     * @return die JSON-Darstellung der Nachricht
     */
    public String toJson(){
        return gson.toJson(this);
    }

    /**
     * Deserialisiert eine Nachricht aus JSON-Format.
     *
     * @param message die JSON-Darstellung der Nachricht
     * @return die deserialisierte JsonMessage-Instanz
     */
    public static JsonMessage fromJson(String message){
        return gson.fromJson(message, JsonMessage.class);
    }
}
