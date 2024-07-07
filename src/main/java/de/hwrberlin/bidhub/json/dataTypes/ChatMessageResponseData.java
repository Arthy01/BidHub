package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das ChatMessageResponseData-Record speichert die Daten einer empfangenen Chat-Nachricht.
 *
 * @param message die empfangene Nachricht
 * @param senderUsername der Benutzername des Absenders
 * @param time die Zeit, zu der die Nachricht gesendet wurde
 * @param important gibt an, ob die Nachricht als wichtig markiert ist
 * @param recipient der Empfänger der Nachricht
 */
public record ChatMessageResponseData(String message, String senderUsername, String time, boolean important, String recipient) {
}
