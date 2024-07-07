package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das RoomClosedResponseData-Record speichert die Daten, die bei der Schließung eines Raums gesendet werden.
 *
 * @param reason der Grund für die Schließung des Raums
 */
public record RoomClosedResponseData(String reason) {
}
