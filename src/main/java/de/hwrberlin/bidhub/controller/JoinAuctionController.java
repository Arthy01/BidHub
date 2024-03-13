package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.FxmlRef;
import de.hwrberlin.bidhub.util.Pair;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class JoinAuctionController implements Initializable {
    @FXML
    private VBox fxScrollPaneContent;
    @FXML
    private TextField fxPrivateRoomId;
    @FXML
    private TextField fxPrivateRoomPassword;
    @FXML
    private Button fxJoinPrivateRoom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createPreviews();
    }

    private void createPreviews(){

    }

    private void createPreview(AuctionRoomInfo info){
        Pair<Node, AuctionRoomPreviewController> instance = FxmlRef.GetInstance(FxmlFile.AuctionRoomPreview);
        // Todo pass event to join auction
        fxScrollPaneContent.getChildren().add(instance.getKey());
    }
}
