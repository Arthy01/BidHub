package de.hwrberlin.bidhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Der Controller für das Info-Popup. Zeigt eine Informationsnachricht an und ermöglicht das Schließen des Popups.
 */
public class InfoPopupController {
    @FXML
    private Label fxInfo;
    @FXML
    private Button fxOK;

    /**
     * Initialisiert das Info-Popup mit der angegebenen Nachricht und der zugehörigen Stage.
     *
     * @param info Die anzuzeigende Information.
     * @param stage Die Stage, das das Popup darstellt.
     */
    public void initialize(String info, Stage stage){
        fxInfo.setText(info);
        fxOK.setOnAction((e) -> stage.close());
    }
}
