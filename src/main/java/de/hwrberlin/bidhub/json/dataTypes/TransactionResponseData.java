package de.hwrberlin.bidhub.json.dataTypes;

import java.util.ArrayList;

/**
 * Das TransactionResponseData-Record speichert die Antwortdaten für eine Transaktionsanfrage.
 *
 * @param transactionData eine Liste der Transaktionsdaten
 */
public record TransactionResponseData(ArrayList <TransactionData> transactionData) {
}
