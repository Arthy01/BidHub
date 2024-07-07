package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.UserInformation;

/**
 * Das UserInformationUpdateRequestData-Record speichert die Daten für eine Anfrage zur Aktualisierung der Benutzerinformationen.
 *
 * @param senderId die ID des anfragenden Benutzers
 * @param information die neuen Benutzerinformationen
 */
public record UserInformationUpdateRequestData(long senderId, UserInformation information) {
}
