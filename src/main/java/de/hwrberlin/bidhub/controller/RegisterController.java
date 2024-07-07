package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.model.client.LoginHandler;
import de.hwrberlin.bidhub.model.client.RegisterHandler;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Der RegisterController ist für die Handhabung der Benutzeroberfläche zur Registrierung
 * eines neuen Benutzers zuständig.
 */
public class RegisterController implements Initializable {
    @FXML
    private TextField fxUsername;
    @FXML
    private TextField fxEmail;
    @FXML
    private PasswordField fxPassword;
    @FXML
    private Label fxErrorMsg;
    @FXML
    private Button fxRegister;
    @FXML
    private Hyperlink fxLogin;
    private final LoginHandler loginHandler = new LoginHandler();
    private final RegisterHandler registerHandler = new RegisterHandler();

    /**
     * Initialisiert den Controller und richtet die Aktionen für die Registrierungselemente ein.
     *
     * @param url die URL zur Initialisierung
     * @param resourceBundle das zu ladende Ressourcenbündel
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxRegister.setOnAction(this::onRegisterButtonPressed);
        fxLogin.setOnAction(this::onLoginLinkPressed);
        fxPassword.setOnKeyPressed(this::onPasswordKeyPressed);
    }

    /**
     * Wird aufgerufen, wenn der Benutzer auf den Registrierungsbutton klickt.
     *
     * @param event das auslösende Ereignis
     */
    private void onRegisterButtonPressed(ActionEvent event) {
        if(registerHandler.validateRegister(fxUsername.getText(), fxPassword.getText(), fxEmail.getText())) {
            login();
        }
        else {
            fxErrorMsg.setVisible(true);
        }
    }

    /**
     * Wird aufgerufen, wenn der Benutzer eine Taste im Passwortfeld drückt.
     *
     * @param keyEvent das auslösende Tastaturereignis
     */
    private void onPasswordKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER) {
            onRegisterButtonPressed(null);
        }
    }

    /**
     * Führt den Login durch und wechselt zur Dashboard-Ansicht.
     */
    private void login() {
        ApplicationClient client = loginHandler.validateLogin(fxUsername.getText(), fxPassword.getText());

        ClientApplication.setApplicationClient(client);
        StageManager.createStage(FxmlFile.Dashboard, "Dashboard", true);
    }

    /**
     * Wird aufgerufen, wenn der Benutzer auf den Login-Link klickt.
     *
     * @param event das auslösende Ereignis
     */
    private void onLoginLinkPressed(ActionEvent event) {
        StageManager.createStage(FxmlFile.Login, "Login",true);
    }
}