package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das TransactionData-Record speichert die Daten einer Transaktion.
 *
 * @param sellerID die ID des Verkäufers
 * @param sellerUsername der Benutzername des Verkäufers
 * @param buyerUsername der Benutzername des Käufers
 * @param productName der Name des Produkts
 * @param price der Preis des Produkts
 */
public record TransactionData(long sellerID, String sellerUsername, String buyerUsername, String productName, double price) {
}
