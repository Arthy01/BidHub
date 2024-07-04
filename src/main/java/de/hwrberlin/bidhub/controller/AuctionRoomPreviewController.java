package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.model.client.AuctionRoomHandler;
import de.hwrberlin.bidhub.model.client.JoinAuctionHandler;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Der Controller für die Vorschau eines Auktionsraums. Verwaltet die Anzeige der Raumdetails und die Beitrittsfunktion.
 */
public class AuctionRoomPreviewController{
    @FXML
    private Label fxId;
    @FXML
    private Label fxTitle;
    @FXML
    private Label fxDescription;
    @FXML
    private Label fxClientCount;
    @FXML
    private Button fxJoinRoom;

    /**
     * Initialisiert die Vorschau mit den Informationen des Auktionsraums.
     *
     * @param info Die Informationen des Auktionsraums.
     */
    public void initialize(AuctionRoomInfo info){
        fxId.setText(info.getId());
        fxTitle.setText(info.getTitle());
        fxDescription.setText(info.getDescription());
        fxClientCount.setText(String.valueOf(info.getCurrentClients()));

        fxJoinRoom.setOnAction(e -> JoinAuctionHandler.joinAuction(info.getId()));
    }
}
