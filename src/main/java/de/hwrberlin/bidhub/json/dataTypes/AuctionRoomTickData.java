package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das AuctionRoomTickData-Record speichert die verbleibende Zeit in Sekunden für einen Tick in einem Auktionsraum.
 *
 * @param remainingSeconds die verbleibenden Sekunden
 */
public record AuctionRoomTickData (int remainingSeconds){
}
