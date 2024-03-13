package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.json.JsonMessage;
import org.java_websocket.WebSocket;

public record CallbackContext(JsonMessage message, WebSocket conn) {
}
