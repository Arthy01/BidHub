package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.model.client.CreateAuctionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Der Controller für das Erstellen einer Auktion. Verwaltet die Eingabefelder und Aktionen zum Erstellen einer neuen Auktion.
 */
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

    private final CreateAuctionHandler handler = new CreateAuctionHandler();

    /**
     * Initialisiert die Ansicht und setzt die erforderlichen Event-Handler.
     *
     * @param url Die URL der FXML-Ressource.
     * @param resourceBundle Das Ressourcenpaket.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupVisibilityChoiceBox();
        fxCreateAuction.setOnAction(this::onCreateAuctionButtonPressed);
    }

    /**
     * Richtet die Auswahlbox für die Sichtbarkeit der Auktion ein.
     */
    private void setupVisibilityChoiceBox(){
        fxVisibility.getItems().addAll("Öffentlich", "Privat");
        fxVisibility.getSelectionModel().selectFirst();

        fxVisibility.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Öffentlich".equals(newValue)) {
                changeVisibility(false);
            }
            else if ("Privat".equals(newValue)) {
                changeVisibility(true);
            }
        });

        changeVisibility(false);
    }

    /**
     * Ändert die Sichtbarkeit des Passwortfelds basierend auf der ausgewählten Sichtbarkeit.
     *
     * @param isPrivate Gibt an, ob die Auktion privat ist.
     */
    private void changeVisibility(boolean isPrivate){
        fxPassword.setVisible(isPrivate);
        fxPassword.setText("");
    }

    /**
     * Behandelt das Drücken des Erstellen-Buttons und erstellt eine neue Auktion.
     *
     * @param event Das ActionEvent, das die Methode ausgelöst hat.
     */
    private void onCreateAuctionButtonPressed(ActionEvent event){
        handler.createAuction(fxTitle.getText(), fxDescription.getText(), fxPassword.getText());
    }
}
