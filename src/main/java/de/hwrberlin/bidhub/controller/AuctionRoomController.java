package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.ChatMessageResponseData;
import de.hwrberlin.bidhub.model.client.AuctionRoomHandler;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.FxmlRef;
import de.hwrberlin.bidhub.util.Pair;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


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

    private AuctionRoomHandler handler;

    public void initialize(String roomId) {
        handler = new AuctionRoomHandler(roomId);

        registerCallbacks();

        if (handler.getIsInitiator()){
            setupForInitiator();
        }
        else{
            setupForParticipant();
        }

        setupShared();
        updateRoomInfo();
    }

    private void registerCallbacks(){
        ClientApplication.getSocketManager().registerCallback(CallbackType.Client_OnRoomClosed.name(), this::onRoomClosed);
        ClientApplication.getSocketManager().registerCallback(CallbackType.Client_ReceiveChatMessage.name(), this::onChatMessageReceived);
    }

    private void unregisterCallbacks(){
        ClientApplication.getSocketManager().unregisterCallback(CallbackType.Client_OnRoomClosed.name());
        ClientApplication.getSocketManager().unregisterCallback(CallbackType.Client_ReceiveChatMessage.name());
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
            String errorMessage = handler.sendChatMessage(fxChatInput.getText());
            fxChatInput.setText("");

            if (errorMessage.isBlank())
                return;
            else
                instantiateChatMessage("[SYSTEM] " + errorMessage, true);
        }
    }

    private void onBidInputKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER) {
            onBidInputButtonPressed(null);
        }
    }

    private void onChatMessageReceived(JsonMessage msg){
        Platform.runLater(() -> {
            ChatMessageResponseData data;
            try {
                data = msg.getData();
            } catch (Exception e) {
                System.out.println("Callback auf dem Server nicht registriert!");
                throw new RuntimeException(e);
            }

            String prefix = "[" + data.senderUsername() + "] ";
            if (!data.recipient().isBlank())
                prefix = "[" + data.senderUsername() + " -> " + data.recipient() + "] ";

            instantiateChatMessage("(" + data.time() + ") " + prefix + data.message(), data.important());
        });

        System.out.println("Chat Nachricht im Auction Room " + handler.getRoomId() + " empfangen.");
    }

    private void instantiateChatMessage(String finalMessage, boolean important){
        Pair<Node, ChatMessageController> instance = FxmlRef.GetInstance(FxmlFile.ChatMessage);

        instance.getValue().setText(finalMessage, important);
        fxChatParent.getChildren().add(instance.getKey());

        Platform.runLater( () -> fxChatScrollPane.setVvalue(1));
    }

    private void updateRoomInfo(){
        AuctionRoomInfo info = handler.getAuctionRoomInfo();

        if (info == null)
            return;

        fxRoomTitle.setText("Auktionsraum " + info.getId());
        updateClientInfos(info);
        System.out.println("Raum info für Auction Room " + info.getId() + " aktualisiert.");
    }

    private void updateClientInfos(AuctionRoomInfo roomInfo){
        // TODO update client infos (show which clients are in room etc)
    }

    private void updateClientInfos(){
        AuctionRoomInfo info = handler.getAuctionRoomInfo();

        if (info == null)
            return;

        updateClientInfos(info);
    }

    private void onBidInputButtonPressed(ActionEvent event){
        // place bid
    }

    private void onLeaveRoomButtonPressed(ActionEvent event){
        unregisterCallbacks();
        handler.leaveRoom();
    }

    private void onRoomClosed(JsonMessage msg){
        if (ClientApplication.getApplicationClient().getCurrentConnectedRoom().isBlank())
            return;

        unregisterCallbacks();
        Platform.runLater(handler::leaveRoom);
    }
}
