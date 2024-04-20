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

public class UserInformationHandler {
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
