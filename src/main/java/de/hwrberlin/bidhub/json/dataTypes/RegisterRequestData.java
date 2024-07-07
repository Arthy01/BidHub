package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das RegisterRequestData-Record speichert die Registrierungsdaten eines neuen Benutzers.
 *
 * @param username der Benutzername des neuen Benutzers
 * @param hashedPassword das gehashte Passwort des neuen Benutzers
 * @param email die E-Mail-Adresse des neuen Benutzers
 */
public record RegisterRequestData(String username, String hashedPassword, String email) {
}
