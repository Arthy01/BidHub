package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Button fxLogout;
    @FXML
    private Button fxProfile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxLogout.setOnAction(e -> ClientApplication.logout());
    }
}
