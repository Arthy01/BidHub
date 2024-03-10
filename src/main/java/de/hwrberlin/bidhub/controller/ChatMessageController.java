package de.hwrberlin.bidhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ChatMessageController {
    @FXML
    private Label fxMessage;

    public void setText(String text, boolean important){
        if (important)
            fxMessage.setStyle("-fx-font-weight: bold;");

        fxMessage.setText(text);
    }
}
