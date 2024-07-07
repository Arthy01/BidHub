package de.hwrberlin.bidhub.json.dataTypes;

import java.io.Serializable;

/**
 * Das AuctionRoomClientData-Record speichert die Daten eines Clients in einem Auktionsraum.
 *
 * @param isInitiator gibt an, ob der Client der Initiator des Auktionsraums ist
 * @param username der Benutzername des Clients
 */
public record AuctionRoomClientData(
        boolean isInitiator,
        String username

) implements Serializable {}
