package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.ApplicationClient;

public record ProductInfo(String title, String description, ApplicationClient seller, long id) {
}
