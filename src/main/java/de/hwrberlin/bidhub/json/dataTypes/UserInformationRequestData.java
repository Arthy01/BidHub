package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.UserInformation;

/**
 * Das UserInformationRequestData-Record speichert die Daten für eine Anfrage nach Benutzerinformationen.
 *
 * @param senderID die ID des anfragenden Benutzers
 * @param requestUserID die ID des Benutzers, dessen Informationen angefordert werden
 */
public record UserInformationRequestData(
   long senderID,
   long requestUserID
) {}
