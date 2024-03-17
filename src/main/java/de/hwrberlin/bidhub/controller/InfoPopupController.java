package de.hwrberlin.bidhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InfoPopupController {
    @FXML
    private Label fxInfo;
    @FXML
    private Button fxOK;

    public void initialize(String info, Stage stage){
        fxInfo.setText(info);
        fxOK.setOnAction((e) -> stage.close());
    }
}
