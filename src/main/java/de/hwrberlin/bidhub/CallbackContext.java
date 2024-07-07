package de.hwrberlin.bidhub;

import de.hwrberlin.bidhub.json.JsonMessage;
import org.java_websocket.WebSocket;

/**
 * Der CallbackContext speichert den Kontext eines Callbacks, der eine Nachricht und eine WebSocket-Verbindung umfasst.
 *
 * @param message die JsonMessage, die den Callback auslöst
 * @param conn die WebSocket-Verbindung, über die die Nachricht empfangen wurde
 */
public record CallbackContext(JsonMessage message, WebSocket conn) {
}
