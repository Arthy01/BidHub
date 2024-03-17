package de.hwrberlin.bidhub.json;

import com.google.gson.Gson;
import de.hwrberlin.bidhub.model.shared.CallbackType;

import java.util.UUID;

public class JsonMessage {
    private static final Gson gson = new Gson();

    private final String messageId;
    private String responseId;
    private final String callbackType;
    private final String data;
    private final String dataType;

    public JsonMessage(String callbackType){
        messageId = UUID.randomUUID().toString();
        this.callbackType = callbackType;

        responseId = "";
        data = "";
        dataType = "";
    }

    public JsonMessage(String callbackType, Object data, String dataType){
        messageId = UUID.randomUUID().toString();
        this.callbackType = callbackType;

        responseId = "";
        this.data = gson.toJson(data);
        this.dataType = dataType;
    }

    public String getMessageId(){
        return messageId;
    }

    public String getCallbackType(){
        return callbackType;
    }

    public <T> T getData() throws Exception {
        System.out.println("Konvertierung in: " + dataType);
        return (T) gson.fromJson(data, Class.forName(dataType));
    }

    public String getResponseId() {
        return responseId;
    }

    public JsonMessage setResponseId(String responseId) {
        this.responseId = responseId;
        return this;
    }

    public String toJson(){
        return gson.toJson(this);
    }

    public static JsonMessage fromJson(String message){
        return gson.fromJson(message, JsonMessage.class);
    }
}
