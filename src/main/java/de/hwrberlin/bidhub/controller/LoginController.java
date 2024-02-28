package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.RMIInfo;
import de.hwrberlin.bidhub.model.shared.ILoginHandler;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.application.Platform;
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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;

/**
 * Controller class for the login screen of the application.
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

    private ILoginHandler remoteLoginHandler;

    /**
     * Initialization method. Sets up the action handlers for the login button and password field.
     * @param url The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            locateServices();
        }
        catch (RemoteException | NotBoundException e){
            e.printStackTrace();
            return;
        }

        fxLogin.setOnAction(this::onLoginButtonPressed);
        fxPassword.setOnKeyPressed(this::onPasswordKeyPressed);
    }

    private void locateServices() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(RMIInfo.getHost(), RMIInfo.getPort());
        remoteLoginHandler = (ILoginHandler) registry.lookup("LoginService");
    }

    /**
     * Handles the login button press event.
     * If the provided username and password match an existing user, proceeds to the main panel.
     * Otherwise, displays an error message.
     */
    private void onLoginButtonPressed(ActionEvent event) {

        ClientApplication.executor.submit(() -> {
            try {
                boolean isLoginCorrect = remoteLoginHandler.onRequestLogin(fxUsername.getText(), fxPassword.getText());
                if (isLoginCorrect){
                    Platform.runLater(this::onSuccessfulLogin); // Auf dem Main Thread aufrufen
                }
                else {
                    Platform.runLater(this::onFailedLogin); // Auf dem Main Thread aufrufen
                }
            }
            catch (RemoteException e) {
                Platform.runLater(() -> {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                });
            }
        });

        /*
        try {
            Client client = ClientApplication.createClient(fxUsername.getText());
            System.out.println("press");

            boolean isAuthenticated = authenticator.authenticate(new LoginInfo(client, fxUsername.getText(), fxPassword.getText()));
            System.out.println(isAuthenticated);
            if (isAuthenticated){
                System.out.println("Suc");
                Platform.runLater(this::onSuccessfulLogin); // Auf dem Main Thread aufrufen
            }
            else {
                System.out.println("Fl");
                Platform.runLater(this::onFailedLogin); // Auf dem Main Thread aufrufen
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        */
    }

    /**
     * Handles the password field key press event.
     * If the Enter key is pressed, triggers the login button press event.
     * @param keyEvent The KeyEvent object representing the key that was pressed.
     */
    private void onPasswordKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER) {
            onLoginButtonPressed(null);
        }
    }

    /**
     * Transitions to the main panel of the application.
     */
    private void onSuccessfulLogin() {
        System.out.println("Success!");
        StageManager.createStage(FxmlFile.Dashboard, "Dashboard");
    }

    private void onFailedLogin(){
        System.out.println("Fail!");
    }
}
