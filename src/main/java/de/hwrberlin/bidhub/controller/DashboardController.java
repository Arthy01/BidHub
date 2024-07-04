package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.util.Pair;
import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.FxmlRef;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Der Controller für das Dashboard. Verwaltet die Navigation und Anzeige der verschiedenen Dashboard-Ansichten.
 */
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

    /**
     * Initialisiert die Ansicht und setzt die erforderlichen Event-Handler.
     *
     * @param url Die URL der FXML-Ressource.
     * @param resourceBundle Das Ressourcenpaket.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTooltips();
        setupButtons();

        changeDashboardBody(FxmlFile.Overview);
    }

    /**
     * Richtet die Tooltips für die Schaltflächen ein.
     */
    private void setupTooltips(){
        Tooltip.install(fxLogout, new Tooltip("Ausloggen"));
        Tooltip.install(fxProfile, new Tooltip("Profil"));
    }

    /**
     * Richtet die Schaltflächen und deren Aktionen ein.
     */
    private void setupButtons(){
        fxLogout.setOnAction(e -> ClientApplication.logout());
        fxCreateAuction.setOnAction(e -> onCreateAuctionButtonPressed());
        fxJoinAuction.setOnAction(e -> onJoinAuctionButtonPressed());
        fxOverview.setOnAction(e -> onOverviewButtonPressed());
        fxProfile.setOnAction(e -> onProfileButtonPressed());
    }

    /**
     * Behandelt das Drücken der Profil-Schaltfläche und öffnet die Profilansicht.
     */
    private void onProfileButtonPressed(){
        Pair<UserInformationController, Stage> controllerStagePair = StageManager.createPopup(FxmlFile.UserInformation, "Profil");
        controllerStagePair.getKey().setup(controllerStagePair.getValue());
    }

    /**
     * Behandelt das Drücken der Überblick-Schaltfläche und zeigt die Überblicksseite an.
     */
    private void onOverviewButtonPressed(){
        changeDashboardBody(FxmlFile.Overview);
    }

    /**
     * Behandelt das Drücken der Schaltfläche zum Erstellen einer Auktion und zeigt die entsprechende Seite an.
     */
    private void onCreateAuctionButtonPressed(){
        changeDashboardBody(FxmlFile.CreateAuction);
    }

    /**
     * Behandelt das Drücken der Schaltfläche zum Beitreten einer Auktion und zeigt die entsprechende Seite an.
     */
    private void onJoinAuctionButtonPressed(){
        changeDashboardBody(FxmlFile.JoinAuction);
    }

    /**
     * Ändert den angezeigten Inhalt des Dashboards basierend auf der ausgewählten Seite.
     *
     * @param body Die FXML-Datei, die den neuen Inhalt beschreibt.
     */
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
