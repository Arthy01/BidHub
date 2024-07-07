package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das GetAllTransactionsRequestData-Record speichert die Daten für die Anforderung aller Transaktionen eines Benutzers.
 *
 * @param senderID die ID des anfordernden Benutzers
 */
public record GetAllTransactionsRequestData(long senderID) {
}
