package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das AuctionRoomPasswordValidationRequestData-Record speichert die Daten für die Passwortvalidierung eines Auktionsraums.
 *
 * @param hashedPassword das zu validierende gehashte Passwort
 */
public record AuctionRoomPasswordValidationRequestData(String hashedPassword) {
}
