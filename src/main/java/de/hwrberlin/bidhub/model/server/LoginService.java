package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.LoginRequestData;
import de.hwrberlin.bidhub.json.dataTypes.LoginResponseData;
import de.hwrberlin.bidhub.model.shared.CallbackType;

public class LoginService {
    public LoginService(){
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_ValidateLogin, this::validateLogin);
        System.out.println("Login Callbacks registriert!");
    }

    private void validateLogin(CallbackContext context){
        LoginRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Login for: " + data.username());

        LoginResponseData response = new LoginResponseData(true);
        context.conn().send(new JsonMessage(CallbackType.Client_Response, response, LoginResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }
}
