package de.hwrberlin.bidhub.json.dataTypes;

public record RegisterRequestData(String username, String hashedPassword, String email) {
}
