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

public class OverviewModel {
    public ArrayList <TransactionData> getTransactionData () {
        JsonMessage msg = new JsonMessage(CallbackType.Server_GetTransactionRequest.name(), new GetAllTransactionsRequestData(ClientApplication.getApplicationClient().getId()), GetAllTransactionsRequestData.class.getName());
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        TransactionResponseData transactionResponse;
        try {
            transactionResponse = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Callback auf dem Server nicht registriert!");
            throw new RuntimeException(e);
        }
        return transactionResponse.transactionData();
    }
}
