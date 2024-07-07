package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das KickBanRequestData-Record speichert die Daten für die Anforderung eines Kick- oder Ban-Befehls.
 *
 * @param username der Benutzername des zu kickenden oder zu bannenden Benutzers
 */
public record KickBanRequestData(String username) {
}
