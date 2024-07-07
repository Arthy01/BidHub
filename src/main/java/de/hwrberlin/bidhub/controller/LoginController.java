package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import de.hwrberlin.bidhub.model.client.LoginHandler;
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
 * Der LoginController ist für die Handhabung der Benutzeroberfläche zum Anmelden
 * zuständig.
 */
public class LoginController implements Initializable {
    @FXML
    private TextField fxUsername;
    @FXML
    private PasswordField fxPassword;
    @FXML
    private Label fxErrorMsg;
    @FXML
    private Button fxLogin;
    @FXML
    private Hyperlink fxRegister;

    private final LoginHandler handler = new LoginHandler();

    /**
     * Initialisiert den Controller und richtet die Aktionen für die Anmelde- und Registrierungselemente ein.
     *
     * @param url die URL zur Initialisierung
     * @param resourceBundle das zu ladende Ressourcenbündel
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxLogin.setOnAction(this::onLoginButtonPressed);
        fxPassword.setOnKeyPressed(this::onPasswordKeyPressed);
        fxRegister.setOnAction(this::onRegisterLinkPressed);
    }

    /**
     * Wird aufgerufen, wenn der Benutzer auf den Anmeldebutton klickt.
     *
     * @param event das auslösende Ereignis
     */
    private void onLoginButtonPressed(ActionEvent event) {
        ApplicationClient client = handler.validateLogin(fxUsername.getText(), fxPassword.getText());
        if (client != null){
            System.out.println(client.getEmail());
            login(client);
        }
        else{
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
            onLoginButtonPressed(null);
        }
    }

    /**
     * Führt den Login durch und wechselt zur Dashboard-Ansicht.
     *
     * @param client der angemeldete Benutzer
     */
    private void login(ApplicationClient client){
        ClientApplication.setApplicationClient(client);
        StageManager.createStage(FxmlFile.Dashboard, "Dashboard", true);
    }

    /**
     * Wird aufgerufen, wenn der Benutzer auf den Registrierungslink klickt.
     *
     * @param event das auslösende Ereignis
     */
    private void onRegisterLinkPressed(ActionEvent event) {
        StageManager.createStage(FxmlFile.Register, "Registrieren",true);
    }
}
