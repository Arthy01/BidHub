package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.ApplicationClient;

/**
 * Das AuctionRoomRegisterClientRequestData-Record speichert die Daten für die Registrierung eines Clients in einem Auktionsraum.
 *
 * @param client der Client, der registriert werden soll
 */
public record AuctionRoomRegisterClientRequestData(ApplicationClient client) {
}
