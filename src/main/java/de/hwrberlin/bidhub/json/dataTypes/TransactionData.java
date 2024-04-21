package de.hwrberlin.bidhub.json.dataTypes;

public record TransactionData(long sellerID, String sellerUsername, String buyerUsername, String productName, double price) {
}
