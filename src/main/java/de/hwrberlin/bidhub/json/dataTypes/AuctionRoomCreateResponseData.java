package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;

public record AuctionRoomCreateResponseData (boolean success, AuctionRoomInfo roomInfo){
}
