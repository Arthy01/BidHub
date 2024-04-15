package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.json.dataTypes.*;
import de.hwrberlin.bidhub.model.client.AuctionRoomHandler;
import de.hwrberlin.bidhub.model.client.LeaveRoomReason;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.util.*;
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
import javafx.stage.Stage;


public class AuctionRoomController {
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
    private Label fxMinimumBid;
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
    @FXML
    private Button fxStartAuction;

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
        updateAuctionInfo();
    }

    private void registerCallbacks(){
        ClientApplication.getSocketManager().registerCallback(CallbackType.Client_OnRoomClosed.name(), this::onRoomClosed);
        ClientApplication.getSocketManager().registerCallback(CallbackType.Client_ReceiveChatMessage.name(), this::onChatMessageReceived);
        ClientApplication.getSocketManager().registerCallback(CallbackType.Client_OnTick.name(), this::onTick);
        ClientApplication.getSocketManager().registerCallback(CallbackType.Client_OnAuctionStarted.name(), this::onAuctionStarted);
        ClientApplication.getSocketManager().registerCallback(CallbackType.Client_OnAuctionFinished.name(), this::onAuctionFinished);
        ClientApplication.getSocketManager().registerCallback(CallbackType.Client_OnBid.name(), this::onBidDataReceived);
    }

    private void unregisterCallbacks(){
        ClientApplication.getSocketManager().unregisterCallback(CallbackType.Client_OnRoomClosed.name());
        ClientApplication.getSocketManager().unregisterCallback(CallbackType.Client_ReceiveChatMessage.name());
        ClientApplication.getSocketManager().unregisterCallback(CallbackType.Client_OnTick.name());
        ClientApplication.getSocketManager().unregisterCallback(CallbackType.Client_OnAuctionStarted.name());
        ClientApplication.getSocketManager().unregisterCallback(CallbackType.Client_OnAuctionFinished.name());
        ClientApplication.getSocketManager().unregisterCallback(CallbackType.Client_OnBid.name());
    }

    private void setupShared(){
        fxProfile.setTooltip(new Tooltip("Profil"));
        fxRoomInfo.setTooltip(new Tooltip("Raum Informationen"));
        fxLeaveRoom.setOnAction(this::onLeaveRoomButtonPressed);

        fxChatInput.setOnKeyPressed(this::onChatInputKeyPressed);

        fxChatScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.floatValue() < 1)
                fxChatScrollPane.setVvalue(1);
        });
    }

    private void setupForInitiator(){
        fxBidBox.setManaged(false);
        fxBidBox.setVisible(false);

        fxLeaveRoom.setTooltip(new Tooltip("Raum schließen"));

        fxStartAuction.setOnAction(this::onStartAuctionButtonPressed);
    }

    private void setupForParticipant(){
        fxStartAuction.setManaged(false);
        fxStartAuction.setVisible(false);

        fxPlaceBid.setOnAction(this::onBidInputButtonPressed);
        fxBidInput.setOnKeyPressed(this::onBidInputKeyPressed);
    }

    private void onChatInputKeyPressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ENTER && !fxChatInput.getText().isBlank()) {
            String errorMessage = handler.sendChatMessage(fxChatInput.getText());
            fxChatInput.setText("");

            if (!errorMessage.isBlank())
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

    public void instantiateChatMessage(String finalMessage, boolean important){
        Pair<Node, ChatMessageController> instance = FxmlRef.GetInstance(FxmlFile.ChatMessage);

        instance.getValue().setText(finalMessage, important);
        fxChatParent.getChildren().add(instance.getKey());

        Platform.runLater(() -> fxChatScrollPane.setVvalue(1));
    }

    private void updateRoomInfo(){
        AuctionRoomInfo info = handler.getAuctionRoomInfo();

        if (info == null)
            return;

        fxRoomTitle.setText("Auktionsraum " + info.getId());
        System.out.println("Raum info für Auction Room " + info.getId() + " aktualisiert.");
    }

    private void onBidInputButtonPressed(ActionEvent event){
        float bid;
        try{
            bid = Helpers.convertStringToFloat(fxBidInput.getText());
        }
        catch (NumberFormatException e){
            System.out.println("Gebot kann nicht platziert werden (keine Zahl eingegeben)");
            throw new RuntimeException(e);
        }

        if (!handler.placeBid(bid)){
            Pair<InfoPopupController, Stage> popup = StageManager.createPopup(FxmlFile.InfoPopup, "Gebot nicht platziert");
            popup.getKey().initialize("Dein Gebot in Höhe von " + Helpers.formatToEuro(bid) + " konnte nicht platziert werden!", popup.getValue());
        }
        else{
            fxBidInput.setText("");
        }
    }

    private void onLeaveRoomButtonPressed(ActionEvent event){
        unregisterCallbacks();
        handler.leaveRoom(LeaveRoomReason.Self);
    }

    private void onRoomClosed(JsonMessage msg){
        if (ClientApplication.getApplicationClient().getCurrentConnectedRoom().isBlank())
            return;

        unregisterCallbacks();

        LeaveRoomReason reason = LeaveRoomReason.Unspecified;
        RoomClosedResponseData data;
        try {
            data = msg.getData();
            if (data.reason().equals("kick"))
                reason = LeaveRoomReason.Kick;
            else if (data.reason().equals("ban"))
                reason = LeaveRoomReason.Ban;
            else
                reason = LeaveRoomReason.Closed;
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren der RoomClosedRespnseData!");
        }

        LeaveRoomReason finalReason = reason;
        Platform.runLater(() -> handler.leaveRoom(finalReason));
    }

    private void onStartAuctionButtonPressed(ActionEvent event){
        Pair<StartAuctionPopupController, Stage> popup = StageManager.createPopup(FxmlFile.StartAuctionPopup, "Auktion starten");
        popup.getKey().initialize(this, popup.getValue());
    }

    public void startAuction(AuctionInfo auctionInfo){
        handler.startAuction(auctionInfo);
    }

    private void onTick(JsonMessage msg){
        AuctionRoomTickData data;
        try {
            data = msg.getData();
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren der Tick-Data!");
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> updateRemainingTime(data.remainingSeconds()));
    }

    private void onAuctionStarted(JsonMessage msg){
        AuctionInfo auctionInfo;
        try {
            auctionInfo = msg.getData();
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren der Auction Info!");
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            updateAuctionInfo(auctionInfo);
            System.out.println("Auktion hat begonnen!");
        });
    }

    private void updateAuctionInfo(){
        AuctionInfo info = handler.getAuctionInfo();

        if (info == null)
            return;

        updateAuctionInfo(info);
    }

    private void updateAuctionInfo(AuctionInfo auctionInfo){
        fxProductTitle.setText(auctionInfo.getProduct().title());
        fxProductDescription.setText(auctionInfo.getProduct().description());
        fxMinimumIncrement.setText(Helpers.formatToEuro(auctionInfo.getMinimumIncrement()));
        fxMinimumBid.setText(Helpers.formatToEuro(auctionInfo.getMinimumBid()));

        updateCurrentBid(auctionInfo.getBidData());
        updateRemainingTime(auctionInfo.getRemainingSeconds());
    }

    private void onAuctionFinished(JsonMessage msg){
        Platform.runLater(() -> {
            updateRemainingTime(0);
            System.out.println("Auktion wurde beendet!");
        });
    }

    private void updateRemainingTime(int remainingSeconds){
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        String minutesStr = " Minuten ";
        String secondsStr = " Sekunden";

        if (minutes == 1)
            minutesStr = " Minute ";
        if (seconds == 1)
            secondsStr = " Sekunde";

        fxRemainingTime.setText(minutes + minutesStr + seconds + secondsStr);
    }

    private void onBidDataReceived(JsonMessage msg){
        AuctionRoomBidData data;
        try {
            data = msg.getData();
        } catch (Exception e) {
            System.out.println("Fehler beim Konvertieren der Bid Data!");
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            updateCurrentBid(data);
        });
    }

    private void updateCurrentBid(AuctionRoomBidData bidData){
        if (bidData == null)
            fxCurrentBid.setText("/");
        else
            fxCurrentBid.setText(Helpers.formatToEuro(bidData.bid()) + " (" + bidData.client().getUsername() + ")");
    }
}
