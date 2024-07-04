package de.hwrberlin.bidhub.util;

import de.hwrberlin.bidhub.model.shared.NetworkResponse;

/**
 * Diese Klasse wartet synchron auf eine Antwort von einem Netzwerkanfrage.
 * Sie blockiert die Ausführung, bis eine Antwort vorhanden ist.
 */
public class WaitForResponse {
    /**
     * Erstellt eine neue Instanz von WaitForResponse, die blockiert, bis die Netzwerkanfrage eine Antwort erhält.
     *
     * @param response Die Netzwerkantwort, auf die gewartet wird. Die Methode blockiert, bis {@code response.hasResponse()} {@code true} zurückgibt.
     */
    public WaitForResponse(NetworkResponse response){
        while (!response.hasResponse()){

        }
    }
}
