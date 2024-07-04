package de.hwrberlin.bidhub.model.shared;

import de.hwrberlin.bidhub.json.JsonMessage;

public class NetworkResponse {
    /**
     * Repräsentiert eine Netzwerkantwort mit einem JSON-Nachrichtenobjekt.
     * Diese Klasse ermöglicht das Speichern und Abrufen einer Antwort in Form einer {@link JsonMessage}.
     */
    private JsonMessage response = null;

    /**
     * Gibt die gespeicherte Antwort zurück.
     * Diese Methode ist threadsicher.
     *
     * @return Die gespeicherte {@link JsonMessage} Antwort, oder null, falls keine Antwort gesetzt wurde.
     */
    public synchronized JsonMessage getResponse(){
        return response;
    }

    /**
     * Setzt die Antwort für dieses Netzwerkantwort-Objekt.
     * Diese Methode ist threadsicher.
     *
     * @param response Die zu speichernde {@link JsonMessage} Antwort.
     */
    public synchronized void setResponse(JsonMessage response){
        this.response = response;
    }

    /**
     * Überprüft, ob eine Antwort vorhanden ist.
     * Diese Methode ist threadsicher.
     *
     * @return true, wenn eine Antwort gesetzt wurde, sonst false.
     */
    public synchronized boolean hasResponse(){
        return response != null;
    }
}
