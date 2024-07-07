package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das AuctionRoomCreateRequestData-Record speichert die Daten für die Erstellung eines Auktionsraums.
 *
 * @param title der Titel des Auktionsraums
 * @param description die Beschreibung des Auktionsraums
 * @param hashedPassword das gehashte Passwort für den Auktionsraum
 */
public record AuctionRoomCreateRequestData(String title, String description, String hashedPassword) {
}
