package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.UserInformationRequestData;
import de.hwrberlin.bidhub.json.dataTypes.UserInformationResponseData;
import de.hwrberlin.bidhub.model.client.UserInformationHandler;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.model.shared.NetworkResponse;
import de.hwrberlin.bidhub.model.shared.UserInformation;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.StageManager;
import de.hwrberlin.bidhub.util.WaitForResponse;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UserInformationController {
    @FXML
    private TextField fxUsername;
    @FXML
    private TextField fxEmail;
    @FXML
    private TextField fxFirstname;
    @FXML
    private TextField fxLastname;
    @FXML
    private TextField fxIban;
    @FXML
    private TextField fxCountry;
    @FXML
    private TextField fxStreet;
    @FXML
    private TextField fxStreetnumber;
    @FXML
    private TextField fxPostcode;
    @FXML
    private TextField fxCity;
    @FXML
    private Button fxSave;
    @FXML
    private Button fxCancel;
    @FXML
    private Label fxErrorMsg;

    private Stage stage;
    private final UserInformationHandler handler = new UserInformationHandler();

    public void setup(Stage stage){
        this.stage = stage;

        fxSave.setOnAction(e -> onSaveButtonPressed());
        fxCancel.setOnAction(e -> this.stage.close());

        fillCurrentInformations();
    }

    private void fillCurrentInformations(){
        UserInformation information = handler.getUserInformation();
        if (information == null)
            return;

        fxUsername.setText(information.username());
        fxEmail.setText(information.email());
        fxFirstname.setText(information.firstname());
        fxLastname.setText(information.lastname());
        fxIban.setText(information.iban());
        fxCountry.setText(information.country());
        fxStreet.setText(information.street());
        fxStreetnumber.setText(information.streetnumber());
        fxPostcode.setText(information.postcode());
        fxCity.setText(information.city());
    }

    private void onSaveButtonPressed(){
        if (!validateInputs()){
            fxErrorMsg.setVisible(true);
            return;
        }

        UserInformation information = new UserInformation(
                ClientApplication.getApplicationClient().getId(),
                fxUsername.getText(),
                fxEmail.getText(),
                fxFirstname.getText(),
                fxLastname.getText(),
                fxIban.getText(),
                fxCountry.getText(),
                fxStreet.getText(),
                fxStreetnumber.getText(),
                fxPostcode.getText(),
                fxCity.getText()
        );

        if (!handler.updateUserInformation(information)){
            fxErrorMsg.setVisible(true);
            return;
        }

        this.stage.close();
    }

    private boolean validateInputs(){
        if (!Helpers.isEmailValid(fxEmail.getText()))
            return false;

        if (!Helpers.isIBANValid(fxIban.getText()))
            return false;

        if (fxFirstname.getText() == null || fxFirstname.getText().isBlank())
            return false;

        if (fxLastname.getText() == null || fxLastname.getText().isBlank())
            return false;

        if (fxCountry.getText() == null || fxCountry.getText().isBlank())
            return false;

        if (fxStreet.getText() == null || fxStreet.getText().isBlank())
            return false;

        if (fxStreetnumber.getText() == null || fxStreetnumber.getText().isBlank())
            return false;

        if (fxPostcode.getText() == null || fxPostcode.getText().isBlank())
            return false;

        if (fxCity.getText() == null || fxCity.getText().isBlank())
            return false;

        return true;
    }
}
