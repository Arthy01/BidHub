package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.LoginRequestData;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.WaitForResponse;

/**
 * Handles the login process for clients.
 * This class is responsible for validating user login credentials against the server.
 */
public class LoginHandler {
    /**
     * Validates the login credentials of a user.
     * This method sends a login request to the server with the provided username and password.
     * If the credentials are valid, it returns an {@link ApplicationClient} object representing the logged-in user.
     * If the credentials are invalid or an error occurs, it returns null.
     *
     * @param username The username of the user trying to log in.
     * @param password The password of the user trying to log in.
     * @return An {@link ApplicationClient} object if login is successful; otherwise, null.
     */
    public ApplicationClient validateLogin(String username, String password){
        if (username.isBlank() || password.isBlank()){
            return null;
        }

        NetworkResponse response = new NetworkResponse();

        JsonMessage msg = new JsonMessage(CallbackType.Server_ValidateLogin.name(),
                new LoginRequestData(username, Helpers.hashPassword(password)), LoginRequestData.class.getName());
        System.out.println(msg.toJson());

        ClientApplication.getSocketManager().send(msg, response);

        new WaitForResponse(response);

        ApplicationClient data;
        try {
            data = response.getResponse().getData();
        } catch (Exception e) {
            System.out.println("Callback auf dem Server nicht registriert!");
            return null;
        }

        return data;
    }
}
