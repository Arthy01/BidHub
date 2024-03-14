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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxLogin.setOnAction(this::onLoginButtonPressed);
        fxPassword.setOnKeyPressed(this::onPasswordKeyPressed);
        fxRegister.setOnAction(this::onRegisterLinkPressed);
    }

    private void onLoginButtonPressed(ActionEvent event) {
        if (handler.validateLogin(fxUsername.getText(), fxPassword.getText())){
            login();
        }
        else{
            fxErrorMsg.setVisible(true);
        }
    }

    private void onPasswordKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER) {
            onLoginButtonPressed(null);
        }
    }

    private void login(){
        ClientApplication.setApplicationClient(new ApplicationClient(fxUsername.getText()));
        StageManager.createStage(FxmlFile.Dashboard, "Dashboard", true);
    }

    private void onRegisterLinkPressed(ActionEvent event) {
        System.out.println("Test");
        StageManager.createPopup(FxmlFile.Dashboard, "TestPopUp");
    }
}
