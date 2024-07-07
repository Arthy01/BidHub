package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/**
 * Der RoomInformationController ist für die Anzeige der Informationen eines Auktionsraums
 * verantwortlich.
 */
public class RoomInformationController {
    @FXML
    private Label fxTitle;
    @FXML
    private Label fxDescription;
    @FXML
    private VBox fxClientScrollBox;

    /**
     * Aktualisiert die Benutzeroberfläche mit den Informationen des Auktionsraums.
     *
     * @param info die Informationen des Auktionsraums
     */
    public void updateUI(AuctionRoomInfo info){
        fxTitle.setText(info.getTitle());
        fxDescription.setText(info.getDescription());
        setClients(info.getClients());
    }

    /**
     * Setzt die Liste der Clients im Auktionsraum.
     *
     * @param clients eine HashMap mit den Clients und deren Status
     */
    private void setClients(HashMap<String, Boolean> clients){
        for (Map.Entry<String, Boolean> client : clients.entrySet()){
            Label label = new Label(client.getKey());

            if (client.getValue())
                label.setStyle("-fx-font-weight: bold;");

            fxClientScrollBox.getChildren().add(label);
        }
    }
}
