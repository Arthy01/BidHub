package de.hwrberlin.bidhub.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;

public class ChatMessageController {
    @FXML
    private Label fxMessage;

    public void setText(String text, boolean important){
        if (important)
            fxMessage.setStyle("-fx-font-weight: bold;");

        fxMessage.setText(text);
    }
}
