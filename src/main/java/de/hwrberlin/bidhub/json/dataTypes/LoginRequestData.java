package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das LoginRequestData-Record speichert die Anmeldedaten eines Benutzers.
 *
 * @param username der Benutzername des Benutzers
 * @param hashedPassword das gehashte Passwort des Benutzers
 */
public record LoginRequestData(String username, String hashedPassword) {
}
