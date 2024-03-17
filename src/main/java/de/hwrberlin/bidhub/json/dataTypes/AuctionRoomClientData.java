package de.hwrberlin.bidhub.json.dataTypes;

import java.io.Serializable;

public record AuctionRoomClientData(
        boolean isInitiator,
        String username

) implements Serializable {}
