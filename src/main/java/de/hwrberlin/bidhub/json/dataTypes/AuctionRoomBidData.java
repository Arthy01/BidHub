package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.ApplicationClient;

/**
 * Das AuctionRoomBidData-Record speichert die Daten eines Gebots in einem Auktionsraum.
 *
 * @param bid der Betrag des Gebots
 * @param client der Client, der das Gebot abgegeben hat
 */
public record AuctionRoomBidData(float bid, ApplicationClient client) {
}
