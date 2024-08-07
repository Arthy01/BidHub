package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.json.dataTypes.UserInformationRequestData;
import de.hwrberlin.bidhub.json.dataTypes.UserInformationResponseData;
import de.hwrberlin.bidhub.json.dataTypes.UserInformationUpdateRequestData;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.UserInformation;

/**
 * Verwaltet Benutzerinformationen und stellt Dienste für das Abrufen und Aktualisieren dieser Informationen bereit.
 * Registriert Callbacks für entsprechende Anfragen vom Client.
 */
public class UserInformationService {
    /**
     * Initialisiert eine neue Instanz des UserInformationService.
     * Registriert Callbacks für das Abrufen und Aktualisieren von Benutzerinformationen.
     */
    public UserInformationService(){
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_GetUserInformationRequest.name(), this::OnGetUserInformationRequest);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_UpdateUserInformationRequest.name(), this::OnUpdateUserInformationRequest);
    }

    /**
     * Verarbeitet Anfragen zum Abrufen von Benutzerinformationen.
     * Extrahiert die Anfragedaten und sendet die Benutzerinformationen als Antwort zurück.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält die Nachrichtendaten und Verbindungsinformationen.
     */
    private void OnGetUserInformationRequest(CallbackContext context){
        UserInformationRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Falsche daten! (UserInformationRequestData)");
            context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), null, UserInformationResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
            return;
        }

        UserInformation information = UserInformationDAO.create(data.senderID());
        UserInformationResponseData responseData = new UserInformationResponseData(information);

        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), responseData, UserInformationResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }

    /**
     * Verarbeitet Anfragen zum Aktualisieren von Benutzerinformationen.
     * Überprüft die Gültigkeit der Anfragedaten und führt bei Erfolg die Aktualisierung durch.
     * Sendet eine Antwort zurück, die den Erfolg der Operation anzeigt.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält die Nachrichtendaten und Verbindungsinformationen.
     */
    private void OnUpdateUserInformationRequest(CallbackContext context){
        UserInformationUpdateRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Fehler! (OnUpdateUserInformationRequest)");
            context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(false), SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
            return;
        }

        if (data.senderId() != data.information().id()){
            context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(false), SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
            System.out.println("Falsche id!");
            return;
        }

        boolean success = UserInformationDAO.update(data.information());
        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), new SuccessResponseData(success), SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }
}
