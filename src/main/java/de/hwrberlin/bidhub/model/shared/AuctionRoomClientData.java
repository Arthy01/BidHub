package de.hwrberlin.bidhub.model.shared;

import java.io.Serializable;

public record AuctionRoomClientData(
        boolean isInitiator,
        String username

) implements Serializable {}
