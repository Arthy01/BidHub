package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.ApplicationClient;

public record AuctionRoomBidData(float bid, ApplicationClient client) {
}
