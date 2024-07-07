package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.model.client.AuctionRoomHandler;
import de.hwrberlin.bidhub.model.client.JoinAuctionHandler;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.FxmlRef;
import de.hwrberlin.bidhub.util.Pair;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Der JoinAuctionController ist für die Handhabung der Benutzeroberfläche zum Beitritt
 * in Auktionsräume zuständig.
 */
public class JoinAuctionController implements Initializable {
    @FXML
    private VBox fxScrollPaneContent;
    @FXML
    private TextField fxPrivateRoomId;
    @FXML
    private TextField fxPrivateRoomPassword;
    @FXML
    private Button fxJoinPrivateRoom;

    private final JoinAuctionHandler handler = new JoinAuctionHandler();

    /**
     * Initialisiert den Controller und richtet die Aktion für den Beitritt zu einem privaten Raum ein.
     *
     * @param url die URL zur Initialisierung
     * @param resourceBundle das zu ladende Ressourcenbündel
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxJoinPrivateRoom.setOnAction(this::onJoinPrivateRoomPressed);
        createPreviews();
    }

    /**
     * Erstellt Vorschauen für verfügbare Auktionsräume.
     */
    private void createPreviews(){
        ArrayList<AuctionRoomInfo> availableRooms = handler.getAvailableRooms();

        for (AuctionRoomInfo info : availableRooms){
            createPreview(info);
        }
    }

    /**
     * Erstellt eine Vorschau für einen bestimmten Auktionsraum.
     *
     * @param info die Informationen des Auktionsraums
     */
    private void createPreview(AuctionRoomInfo info){
        Pair<Node, AuctionRoomPreviewController> instance = FxmlRef.GetInstance(FxmlFile.AuctionRoomPreview);
        instance.getValue().initialize(info);
        fxScrollPaneContent.getChildren().add(instance.getKey());
    }

    /**
     * Wird aufgerufen, wenn der Benutzer versucht, einem privaten Raum beizutreten.
     *
     * @param event das auslösende Ereignis
     */
    private void onJoinPrivateRoomPressed(ActionEvent event){
        String roomId = fxPrivateRoomId.getText();
        if (handler.validateRoomPassword(roomId, fxPrivateRoomPassword.getText())){
            JoinAuctionHandler.joinAuction(roomId);
        }
    }
}
