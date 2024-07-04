package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.json.dataTypes.UserInformationRequestData;
import de.hwrberlin.bidhub.json.dataTypes.UserInformationResponseData;
import de.hwrberlin.bidhub.json.dataTypes.UserInformationUpdateRequestData;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.model.shared.UserInformation;
import de.hwrberlin.bidhub.util.WaitForResponse;

/**
 * Verwaltet Benutzerinformationen innerhalb der Anwendung.
 * Diese Klasse bietet Methoden zum Abrufen und Aktualisieren der Benutzerinformationen.
 */
public class UserInformationHandler {
    /**
     * Ruft die Benutzerinformationen des aktuellen Anwendungsklienten ab.
     * Sendet eine Anfrage an den Server, um die Benutzerinformationen basierend auf der Client-ID zu erhalten.
     * Bei einem Fehler während der Anfrage wird eine RuntimeException ausgelöst.
     *
     * @return Ein {@link UserInformation} Objekt, das die Benutzerinformationen enthält.
     * @throws RuntimeException bei einem Fehler in der Verarbeitung der Serverantwort.
     */
    public UserInformation getUserInformation(){
        JsonMessage msg = new JsonMessage(CallbackType.Server_GetUserInformationRequest.name(),
                new UserInformationRequestData(ClientApplication.getApplicationClient().getId(), ClientApplication.getApplicationClient().getId()), UserInformationRequestData.class.getName());
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);
        new WaitForResponse(response);

        UserInformationResponseData data;

        try {
            data = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Fehler!");
            throw new RuntimeException(e);
        }

        return data.userInformation();
    }

    /**
     * Aktualisiert die Benutzerinformationen des aktuellen Anwendungsklienten.
     * Sendet eine Anfrage an den Server mit den aktualisierten Benutzerinformationen.
     * Überprüft die Antwort des Servers, um den Erfolg der Aktualisierung zu bestimmen.
     *
     * @param informations Die zu aktualisierenden Benutzerinformationen.
     * @return true, wenn die Aktualisierung erfolgreich war, sonst false.
     */
    public boolean updateUserInformation(UserInformation informations){
        JsonMessage msg = new JsonMessage(CallbackType.Server_UpdateUserInformationRequest.name(),
                new UserInformationUpdateRequestData(ClientApplication.getApplicationClient().getId(), informations), UserInformationUpdateRequestData.class.getName());
        NetworkResponse response = new NetworkResponse();
        ClientApplication.getSocketManager().send(msg, response);
        new WaitForResponse(response);

        SuccessResponseData data;

        try {
            data = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Fehler!");
            return false;
        }

        return data.success();
    }
}
