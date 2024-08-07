package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.GetAllTransactionsRequestData;
import de.hwrberlin.bidhub.json.dataTypes.TransactionData;
import de.hwrberlin.bidhub.json.dataTypes.TransactionResponseData;
import de.hwrberlin.bidhub.model.shared.CallbackType;

import java.util.ArrayList;

/**
 * Verwaltet Transaktionsinformationen und Anfragen dazu.
 * Registriert Callbacks für Transaktionsanfragen und verarbeitet diese entsprechend.
 */
public class TransactionInformationService {

    /**
     * Erstellt eine neue Instanz von TransactionInformationService.
     * Registriert einen Callback für das Ereignis, Transaktionsanfragen zu erhalten.
     */
    public TransactionInformationService() {
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_GetTransactionRequest.name(), this::onGetTransactionRequest);
    }

    /**
     * Verarbeitet eingehende Anfragen für Transaktionsinformationen.
     * Extrahiert die Daten aus der Anfrage, holt die entsprechenden Transaktionsdaten und sendet eine Antwort zurück.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält die Nachrichtendaten und Verbindungsinformationen.
     */
    private void onGetTransactionRequest(CallbackContext context) {
        GetAllTransactionsRequestData data;
        try{
           data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Wrong data! (TransactionInformationService)");
            context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), null, TransactionResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
            return;
        }

        ArrayList<TransactionData> trans = TransactionDAO.getAllTransactionInformation(data.senderID());
        TransactionResponseData response = new TransactionResponseData(trans);

        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), response, TransactionResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }
}
