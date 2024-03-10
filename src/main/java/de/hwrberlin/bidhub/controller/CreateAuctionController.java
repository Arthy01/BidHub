package de.hwrberlin.bidhub.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateAuctionController implements Initializable {
    @FXML
    private ChoiceBox<String> fxVisibility;
    @FXML
    private TextField fxPassword;
    @FXML
    private TextField fxTitle;
    @FXML
    private TextArea fxDescription;
    @FXML
    private Button fxCreateAuction;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupVisibilityChoiceBox();
    }

    private void setupVisibilityChoiceBox(){
        fxVisibility.getItems().addAll("Öffentlich", "Privat");
        fxVisibility.getSelectionModel().selectFirst();

        fxVisibility.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Öffentlich".equals(newValue)) {
                changeVisibility(false);
            } else if ("Privat".equals(newValue)) {
                changeVisibility(true);
            }
        });

        changeVisibility(false);
    }

    private void changeVisibility(boolean isPrivate){
        fxPassword.setVisible(isPrivate);
    }
}
