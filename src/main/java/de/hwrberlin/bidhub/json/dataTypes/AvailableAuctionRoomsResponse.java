package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;

import java.util.ArrayList;

/**
 * Das AvailableAuctionRoomsResponse-Record speichert die Antwortdaten für verfügbare Auktionsräume.
 *
 * @param infos eine Liste mit Informationen zu den verfügbaren Auktionsräumen
 */
public record AvailableAuctionRoomsResponse(ArrayList<AuctionRoomInfo> infos) {
}
