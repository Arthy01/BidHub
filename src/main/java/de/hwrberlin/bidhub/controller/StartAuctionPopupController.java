package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.dataTypes.AuctionInfo;
import de.hwrberlin.bidhub.json.dataTypes.ProductInfo;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.Pair;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

public class StartAuctionPopupController {
    @FXML
    private TextField fxTitle;
    @FXML
    private TextArea fxDescription;
    @FXML
    private TextField fxTime;
    @FXML
    private ChoiceBox<String> fxTimeUnit;
    @FXML
    private TextField fxMinimumIncrement;
    @FXML
    private TextField fxMinimumBid;
    @FXML
    private Button fxStart;
    @FXML
    private Button fxCancel;

    private AuctionRoomController auctionRoomController;
    private Stage stage;

    public void initialize(AuctionRoomController roomController, Stage stage){
        auctionRoomController = roomController;
        this.stage = stage;
        fxStart.setOnAction(this::onStartButtonPressed);
        fxCancel.setOnAction(this::onCancelButtonPressed);
        setupTimeUnitChoiceBox();
    }

    private void setupTimeUnitChoiceBox(){
        fxTimeUnit.getItems().addAll("Minuten", "Sekunden");
        fxTimeUnit.getSelectionModel().selectFirst();
    }

    private void onStartButtonPressed(ActionEvent event){
        AuctionInfo info = validateInputs();

        if (info == null){
            Pair<InfoPopupController, Stage> popup = StageManager.createPopup(FxmlFile.InfoPopup, "Ungültige Eingaben");
            popup.getKey().initialize("Deine Eingaben sind ungültig, überprüfe sie nocheinmal", popup.getValue());
            return;
        }

        auctionRoomController.startAuction(info);
        stage.close();
    }

    private void onCancelButtonPressed(ActionEvent event){
        stage.close();
    }

    private AuctionInfo validateInputs(){
        String title = fxTitle.getText();

        if (title.isBlank())
            return null;

        String description = fxDescription.getText();

        if (description.isBlank())
            return null;

        float startTime, minimumIncrement, minimumBid;

        try{
            startTime = Helpers.convertStringToFloat(fxTime.getText());
            minimumIncrement = Helpers.convertStringToFloat(fxMinimumIncrement.getText());
            minimumBid = Helpers.convertStringToFloat(fxMinimumBid.getText());
        }
        catch (NumberFormatException e){
            return null;
        }

        TimeUnit timeUnit;

        if (fxTimeUnit.getSelectionModel().getSelectedItem().equals("Minuten")){
            timeUnit = TimeUnit.MINUTES;
        }
        else{
            timeUnit = TimeUnit.SECONDS;
        }

        return new AuctionInfo(new ProductInfo(title, description, ClientApplication.getApplicationClient(), -1),
                startTime, timeUnit, minimumIncrement, minimumBid);
    }
}
