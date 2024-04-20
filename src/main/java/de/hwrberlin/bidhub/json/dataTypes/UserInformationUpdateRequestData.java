package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.UserInformation;

public record UserInformationUpdateRequestData(long senderId, UserInformation information) {
}
