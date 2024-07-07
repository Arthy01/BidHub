package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;

/**
 * Das AuctionRoomCreateResponseData-Record speichert die Antwortdaten für die Erstellung eines Auktionsraums.
 *
 * @param success gibt an, ob die Erstellung erfolgreich war
 * @param roomInfo die Informationen des erstellten Auktionsraums
 */
public record AuctionRoomCreateResponseData (boolean success, AuctionRoomInfo roomInfo){
}
