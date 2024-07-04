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

/**
 * Verwaltet die Registrierung von Benutzern im System.
 * Diese Klasse bietet Methoden zur Validierung und Durchführung der Registrierung von Benutzern.
 */
public class RegisterHandler {
    /**
     * Validiert die Eingaben für die Registrierung eines neuen Benutzers.
     * Überprüft, ob das Passwort, die E-Mail-Adresse und der Benutzername den Anforderungen entsprechen.
     * Leitet die Registrierung weiter, wenn die Validierung erfolgreich ist.
     *
     * @param username Der Benutzername des neuen Benutzers.
     * @param password Das Passwort des neuen Benutzers.
     * @param email Die E-Mail-Adresse des neuen Benutzers.
     * @return true, wenn die Registrierung erfolgreich validiert und durchgeführt wurde, sonst false.
     */
    public boolean validateRegister(String username, String password, String email) {
        if (password.isBlank())
            return false;

        if (!Helpers.isEmailValid(email))
            return false;

        if (username.isBlank())
            return false;

        return performRegister(username, password, email);
    }

    /**
     * Führt die Registrierung eines neuen Benutzers durch.
     * Sendet eine Anfrage an den Server mit den Registrierungsdetails und wartet auf eine Antwort.
     * Überprüft die Antwort des Servers, um den Erfolg der Registrierung zu bestimmen.
     *
     * @param username Der Benutzername des neuen Benutzers.
     * @param password Das Passwort des neuen Benutzers.
     * @param email Die E-Mail-Adresse des neuen Benutzers.
     * @return true, wenn die Registrierung beim Server erfolgreich war, sonst false.
     */
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
