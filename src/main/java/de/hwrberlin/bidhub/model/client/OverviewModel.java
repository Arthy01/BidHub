package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.GetAllTransactionsRequestData;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.json.dataTypes.TransactionData;
import de.hwrberlin.bidhub.json.dataTypes.TransactionResponseData;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.WaitForResponse;

import java.util.ArrayList;

/**
 * Represents the model for the overview section of the client application.
 * This class is responsible for handling the retrieval of transaction data from the server.
 * It communicates with the server to fetch and process transaction data related to the current application client.
 */
public class OverviewModel {
    /**
     * Retrieves transaction data for the current application client.
     * This method sends a request to the server to fetch all transactions associated with the client's ID.
     * It waits for the server's response and then processes it to extract transaction data.
     * If the server response cannot be processed, it prints an error message and throws a RuntimeException.
     *
     * @return An ArrayList of {@link TransactionData} containing the transaction details.
     * @throws RuntimeException if there is an error in processing the server response.
     */
    public ArrayList <TransactionData> getTransactionData () {
        JsonMessage msg = new JsonMessage(CallbackType.Server_GetTransactionRequest.name(), new GetAllTransactionsRequestData(ClientApplication.getApplicationClient().getId()), GetAllTransactionsRequestData.class.getName());
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        TransactionResponseData transactionResponse;
        try {
            transactionResponse = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Could not convert transaction data!");
            throw new RuntimeException(e);
        }
        return transactionResponse.transactionData();
    }
}
