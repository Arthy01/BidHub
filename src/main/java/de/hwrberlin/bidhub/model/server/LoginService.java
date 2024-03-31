package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.LoginRequestData;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import de.hwrberlin.bidhub.model.shared.ApplicationClientDAO;
import de.hwrberlin.bidhub.model.shared.CallbackType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {
    public LoginService(){
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_ValidateLogin.name(), this::validateLogin);
        System.out.println("Login Callbacks registriert!");
    }

    private void validateLogin(CallbackContext context){
        LoginRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren eines Loginrequests!");
            throw new RuntimeException(e);
        }

        System.out.println("Login Versuch für Username: " + data.username());

        ApplicationClient response = ApplicationClientDAO.authenticate(data.username(), data.hashedPassword());
        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), response, ApplicationClient.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }
}
