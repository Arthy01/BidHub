package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das ChatMessageRequestData-Record speichert die Daten für das Senden einer Chat-Nachricht.
 *
 * @param message die zu sendende Nachricht
 * @param recipient der Empfänger der Nachricht
 */
public record ChatMessageRequestData (String message, String recipient){
}
