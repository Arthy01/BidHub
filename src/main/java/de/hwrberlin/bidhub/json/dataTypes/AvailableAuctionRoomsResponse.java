package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;

import java.util.ArrayList;

public record AvailableAuctionRoomsResponse(ArrayList<AuctionRoomInfo> infos) {
}
