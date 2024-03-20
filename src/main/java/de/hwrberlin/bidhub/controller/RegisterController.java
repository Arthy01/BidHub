package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.model.client.ApplicationClient;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterController implements Initializable {
    @FXML
    TextField fxUsername;
    @FXML
    TextField fxEmail;
    @FXML
    PasswordField fxPassword;
    @FXML
    private Label fxErrorMsg;
    @FXML
    private Button fxRegister;
    @FXML
    private Hyperlink fxLogin;

    private final LoginHandler handler = new LoginHandler();

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxRegister.setOnAction(this::onRegisterButtonPressed);
        fxLogin.setOnAction(this::onLoginLinkPressed);
        fxPassword.setOnKeyPressed(this::onPasswordKeyPressed);
    }

    private boolean validateEmail(String email) {
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    private void onRegisterButtonPressed(ActionEvent event) {
        if(validateEmail(fxEmail.getText()) && handler.validateLogin(fxUsername.getText(), fxPassword.getText())) {
            register();
        } else {
            fxErrorMsg.setVisible(true);
        }
    }

    private void onPasswordKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER) {
            onRegisterButtonPressed(null);
        }
    }

    private void register() {
        ClientApplication.setApplicationClient(new ApplicationClient(fxUsername.getText()));
        StageManager.createStage(FxmlFile.Dashboard, "Dashboard", true);
    }

    private void onLoginLinkPressed(ActionEvent event) {
        StageManager.createStage(FxmlFile.Login, "Login",true);
    }
}