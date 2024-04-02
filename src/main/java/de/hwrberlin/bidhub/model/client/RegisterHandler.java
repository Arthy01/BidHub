package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.RegisterRequestData;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.WaitForResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterHandler {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

    public boolean validateRegister(String username, String password, String email) {
        if (password.isBlank())
            return false;

        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches())
            return false;

        if (username.isBlank())
            return false;

        return performRegister(username, password, email);
    }

    private boolean performRegister(String username, String password, String email){
        JsonMessage msg = new JsonMessage(CallbackType.Server_ValidateRegister.name(), new RegisterRequestData(username, Helpers.hashPassword(password), email), RegisterRequestData.class.getName());
        NetworkResponse networkResponse = new NetworkResponse();

        ClientApplication.getSocketManager().send(msg, networkResponse);
        new WaitForResponse(networkResponse);

        SuccessResponseData response;

        try {
            response = networkResponse.getResponse().getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response.success();
    }
}
