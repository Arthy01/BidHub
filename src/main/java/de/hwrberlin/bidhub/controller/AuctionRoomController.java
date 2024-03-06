package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.model.client.AuctionRoomHandler;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomClient;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class AuctionRoomController {
    @FXML
    private HBox fxBalanceBox;
    @FXML
    private Label fxBalance;
    @FXML
    private Button fxRoomSettings;
    @FXML
    private Button fxProfile;
    @FXML
    private Button fxLeaveRoom;
    @FXML
    private Label fxProductTitle;
    @FXML
    private Label fxProductDescription;
    @FXML
    private ImageView fxProductImage;
    @FXML
    private Label fxCurrentBid;
    @FXML
    private Label fxMinimumIncrement;
    @FXML
    private Label fxRemainingTime;
    @FXML
    private VBox fxBidBox;
    @FXML
    private TextField fxBidInput;
    @FXML
    private Button fxPlaceBid;
    @FXML
    private VBox fxChatParent;
    @FXML
    private TextField fxChatInput;
    @FXML
    private ScrollPane fxChatScrollPane;
    @FXML
    private Label fxRoomTitle;
    @FXML
    private Button fxRoomInfo;

    private AuctionRoomHandler auctionRoomHandler;

    public void initialize(IAuctionRoomService auctionRoomService, boolean isInitiator) {
        try {
            auctionRoomHandler = new AuctionRoomHandler(auctionRoomService, isInitiator, fxChatParent, fxChatScrollPane, this::updateClientInfos);
        }
        catch (RemoteException e){
            ClientApplication.logout();
            e.printStackTrace();
            return;
        }

        if (isInitiator){
            setupForInitiator();
        }
        else{
            setupForParticipant();
        }

        setupShared();
        updateRoomInfo();
    }

    private void setupShared(){
        fxProfile.setTooltip(new Tooltip("Profil"));
        fxRoomInfo.setTooltip(new Tooltip("Raum Informationen"));
        fxLeaveRoom.setOnAction(this::onLeaveRoomButtonPressed);

        fxChatInput.setOnKeyPressed(this::onChatInputKeyPressed);
    }

    private void setupForInitiator(){
        fxBalanceBox.setManaged(false);
        fxBalanceBox.setVisible(false);

        fxBidBox.setManaged(false);
        fxBidBox.setVisible(false);

        fxLeaveRoom.setTooltip(new Tooltip("Raum schließen"));
        fxRoomSettings.setTooltip(new Tooltip("Raum verwalten"));
    }

    private void setupForParticipant(){
        fxRoomSettings.setManaged(false);
        fxRoomSettings.setVisible(false);

        fxPlaceBid.setOnAction(this::onBidInputButtonPressed);
        fxBidInput.setOnKeyPressed(this::onBidInputKeyPressed);
    }

    private void onChatInputKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER && !fxChatInput.getText().isBlank()) {
            auctionRoomHandler.sendMessage(fxChatInput.getText().strip());
            fxChatInput.setText("");
        }
    }

    private void onBidInputKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER) {
            onBidInputButtonPressed(null);
        }
    }

    private void updateRoomInfo(){
        AuctionRoomInfo info = auctionRoomHandler.getRoomInfo();

        if (info == null)
            return;

        fxRoomTitle.setText("Auktionsraum " + info.getDisplayId());
        updateClientInfos(info);
    }

    private void updateClientInfos(AuctionRoomInfo roomInfo){
        System.out.println("Update client Data");
        // TODO update client infos (show which clients are in room etc)
    }

    private void updateClientInfos(){
        AuctionRoomInfo info = auctionRoomHandler.getRoomInfo();

        if (info == null)
            return;

        updateClientInfos(info);
    }

    private void onBidInputButtonPressed(ActionEvent event){
        // place bid
    }

    private void onLeaveRoomButtonPressed(ActionEvent event){
        auctionRoomHandler.onLeaveRoom();
    }
}
