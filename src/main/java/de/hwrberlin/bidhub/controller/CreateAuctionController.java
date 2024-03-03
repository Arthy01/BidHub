package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.exceptions.InvalidInputException;
import de.hwrberlin.bidhub.model.client.CreateAuctionHandler;
import de.hwrberlin.bidhub.util.Helpers;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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

    private CreateAuctionHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            handler = new CreateAuctionHandler();
        }
        catch (RemoteException | NotBoundException e) {
            ClientApplication.logout();
            e.printStackTrace();
            return;
        }

        setupVisibilityChoiceBox();

        fxCreateAuction.setOnAction(e -> {
            try {
                handler.createAuction(fxTitle.getText(), fxDescription.getText(), fxPassword.getText());
            }
            catch (InvalidInputException ex) {
                System.out.println(ex.getMessage());
            }
        });
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
        handler.changeVisibility(isPrivate);
        fxPassword.setVisible(isPrivate);
    }
}
