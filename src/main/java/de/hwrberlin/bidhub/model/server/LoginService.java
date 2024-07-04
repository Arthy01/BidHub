package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.CallbackContext;
import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.LoginRequestData;
import de.hwrberlin.bidhub.json.dataTypes.RegisterRequestData;
import de.hwrberlin.bidhub.json.dataTypes.SuccessResponseData;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import de.hwrberlin.bidhub.model.shared.CallbackType;

/**
 * Verwaltet die Login- und Registrierungsfunktionen des Servers.
 * Diese Klasse bietet Methoden zur Validierung von Login- und Registrierungsanfragen.
 * Sie interagiert mit dem {@link ServerApplication} für die Callback-Registrierung und verwendet {@link ApplicationClientDAO} für Datenbankoperationen.
 */
public class LoginService {
    /**
     * Initialisiert eine neue Instanz des LoginService.
     * Registriert Callbacks für die Validierung von Login- und Registrierungsanfragen.
     */
    public LoginService(){
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_ValidateLogin.name(), this::validateLogin);
        ServerApplication.getSocketManager().registerCallback(CallbackType.Server_ValidateRegister.name(), this::validateRegister);
        System.out.println("Login Callbacks registriert!");
    }

    /**
     * Validiert die Login-Anfrage eines Benutzers.
     * Diese Methode extrahiert die Login-Daten aus der Anfrage, führt eine Authentifizierung durch und sendet eine Antwort zurück.
     * Bei einem Fehler während der Datenextraktion oder Authentifizierung wird eine Ausnahme geworfen.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält die Nachrichtendaten und die Verbindungsinformationen.
     */
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

    /**
     * Validiert die Registrierungsanfrage eines Benutzers.
     * Diese Methode überprüft, ob der Benutzername verfügbar ist, und führt bei Verfügbarkeit eine Registrierung durch.
     * Eine Erfolgs- oder Fehlerantwort wird zurückgesendet.
     *
     * @param context Der Kontext der Callback-Anfrage, enthält die Nachrichtendaten und die Verbindungsinformationen.
     */
    private void validateRegister(CallbackContext context){
        RegisterRequestData data;
        try {
            data = context.message().getData();
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren eines Registerrequests!");
            throw new RuntimeException(e);
        }

        boolean isUsernameAvailable = ApplicationClientDAO.isAvailable(data.username());
        boolean success = false;

        if (isUsernameAvailable){
            success = performRegister(data);
        }

        SuccessResponseData response = new SuccessResponseData(success);
        context.conn().send(new JsonMessage(CallbackType.Client_Response.name(), response, SuccessResponseData.class.getName()).setResponseId(context.message().getMessageId()).toJson());
    }

    /**
     * Führt die Registrierung eines neuen Benutzers durch.
     * Diese Methode erstellt einen neuen Benutzereintrag in der Datenbank mit den bereitgestellten Daten.
     * Gibt {@code true} zurück, wenn die Registrierung erfolgreich war, andernfalls {@code false}.
     *
     * @param data Die Daten der Registrierungsanfrage, einschließlich Benutzername, gehashtes Passwort und E-Mail.
     * @return {@code true}, wenn die Registrierung erfolgreich war, sonst {@code false}.
     */
    private boolean performRegister(RegisterRequestData data){
        return ApplicationClientDAO.create(data.username(), data.hashedPassword(), data.email());
    }
}
