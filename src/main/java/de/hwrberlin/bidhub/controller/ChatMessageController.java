package de.hwrberlin.bidhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Der Controller für Chatnachrichten. Verwaltet die Anzeige von Nachrichten im Chat.
 */
public class ChatMessageController {
    @FXML
    private Label fxMessage;

    /**
     * Setzt den Text der Nachricht und markiert ihn gegebenenfalls als wichtig.
     *
     * @param text Der anzuzeigende Text.
     * @param important Gibt an, ob die Nachricht als wichtig markiert werden soll.
     */
    public void setText(String text, boolean important){
        if (important)
            fxMessage.setStyle("-fx-font-weight: bold;");

        fxMessage.setText(text);
    }
}
