package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.UserInformation;

/**
 * Das UserInformationResponseData-Record speichert die Antwortdaten einer Benutzerinformationsanfrage.
 *
 * @param userInformation die Informationen des angeforderten Benutzers
 */
public record UserInformationResponseData (UserInformation userInformation){
}
