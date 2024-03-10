package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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

    public void initialize(AuctionRoomInfo info, Runnable onJoinAuctionButtonPressed){
        fxId.setText(info.getDisplayId());
        fxTitle.setText(info.getTitle());
        fxDescription.setText(info.getDescription());
        fxClientCount.setText(String.valueOf(info.getCurrentClients()));

        fxJoinRoom.setOnAction(e -> onJoinAuctionButtonPressed.run());
    }
}
