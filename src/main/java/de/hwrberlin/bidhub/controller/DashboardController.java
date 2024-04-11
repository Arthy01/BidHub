package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.FxmlRef;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Button fxLogout;
    @FXML
    private Button fxProfile;
    @FXML
    private Button fxCreateAuction;
    @FXML
    private Button fxJoinAuction;
    @FXML
    private Button fxOverview;
    @FXML
    private Label fxDashboardTitle;
    @FXML
    private VBox fxDashboardBody;

    private final HashMap<FxmlFile, String> dashboardBodies = new HashMap<>(){{
        put(FxmlFile.Overview, "Überblick");
        put(FxmlFile.JoinAuction, "Auktion beitreten");
        put(FxmlFile.CreateAuction, "Auktion erstellen");
    }};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTooltips();
        setupButtons();

        changeDashboardBody(FxmlFile.Overview);
    }

    private void setupTooltips(){
        Tooltip.install(fxLogout, new Tooltip("Ausloggen"));
        Tooltip.install(fxProfile, new Tooltip("Profil"));
    }

    private void setupButtons(){
        fxLogout.setOnAction(e -> ClientApplication.logout());
        fxCreateAuction.setOnAction(e -> onCreateAuctionButtonPressed());
        fxJoinAuction.setOnAction(e -> onJoinAuctionButtonPressed());
        fxOverview.setOnAction(e -> onOverviewButtonPressed());
    }

    private void onOverviewButtonPressed(){
        changeDashboardBody(FxmlFile.Overview);
    }

    private void onCreateAuctionButtonPressed(){
        //dashboardHandler.createAuction("Test Auction Room", "Nur ein Test!", false);
        changeDashboardBody(FxmlFile.CreateAuction);
    }

    private void onJoinAuctionButtonPressed(){
        changeDashboardBody(FxmlFile.JoinAuction);
    }

    private void onProfileButtonPressed(){
        StageManager.createStage(FxmlFile.UserInformation, "Profil", true);
    }
    private void changeDashboardBody(FxmlFile body){
        if (!dashboardBodies.containsKey(body)){
            System.out.println("Dashboard body " + body + " does not exist!");
            return;
        }

        fxDashboardBody.getChildren().clear();

        Node bodyInstance = FxmlRef.GetInstance(body).getKey();
        VBox.setVgrow(bodyInstance, Priority.ALWAYS);
        fxDashboardBody.getChildren().add(bodyInstance);

        fxDashboardTitle.setText("Dashboard - " + dashboardBodies.get(body));
    }
}
