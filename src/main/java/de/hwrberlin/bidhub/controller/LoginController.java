package de.hwrberlin.bidhub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField fxUsername;
    @FXML
    private PasswordField fxPassword;
    @FXML
    private Label fxErrorMsg;
    @FXML
    private Button fxLogin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxLogin.setOnAction(this::onLoginButtonPressed);
        fxPassword.setOnKeyPressed(this::onPasswordKeyPressed);
    }

    private void onLoginButtonPressed(ActionEvent event) {

    }

    private void onPasswordKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER) {
            onLoginButtonPressed(null);
        }
    }

}
