package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.GetAllTransactionsRequestData;
import de.hwrberlin.bidhub.json.dataTypes.TransactionData;
import de.hwrberlin.bidhub.json.dataTypes.TransactionResponseData;
import de.hwrberlin.bidhub.model.shared.CallbackType;

import java.util.ArrayList;

public class TransactionInformationService {

    public TransactionInformationService() {
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_GetTransactionRequest.name(), this::onGetTransactionRequest);
    }

    private void onGetTransactionRequest(CallbackContext context) {
        GetAllTransactionsRequestData data;
        try{
           data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Falsche Daten! (TransactionInformationService)");
            context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), null, TransactionResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
            return;
        }

        ArrayList<TransactionData> trans = TransactionDAO.getAllTransactionInformation(data.senderID());
        TransactionResponseData response = new TransactionResponseData(trans);

        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), trans, TransactionResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }
}
