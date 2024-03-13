package de.hwrberlin.bidhub.model.shared;

import de.hwrberlin.bidhub.json.JsonMessage;

public class NetworkResponse {
    private JsonMessage response = null;

    public synchronized JsonMessage getResponse(){
        return response;
    }

    public synchronized void setResponse(JsonMessage response){
        this.response = response;
    }

    public synchronized boolean hasResponse(){
        return response != null;
    }
}
