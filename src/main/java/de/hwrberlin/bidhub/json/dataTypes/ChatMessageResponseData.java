package de.hwrberlin.bidhub.json.dataTypes;

public record ChatMessageResponseData(String message, String senderUsername, String time, boolean important, String recipient) {
}
