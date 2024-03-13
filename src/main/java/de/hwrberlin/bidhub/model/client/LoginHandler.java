package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.LoginRequestData;
import de.hwrberlin.bidhub.json.dataTypes.LoginResponseData;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.WaitForResponse;

public class LoginHandler {
    public boolean validateLogin(String username, String password){
        if (username.isBlank() || password.isBlank()){
            return false;
        }

        NetworkResponse response = new NetworkResponse();
        JsonMessage msg = new JsonMessage(CallbackType.Server_ValidateLogin,
                new LoginRequestData(username, Helpers.hashPassword(password)), LoginRequestData.class.getName());
        System.out.println(msg.toJson());
        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        LoginResponseData data;

        try {
            data = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Error");
            return false;
        }

        return data.success();
    }
}
