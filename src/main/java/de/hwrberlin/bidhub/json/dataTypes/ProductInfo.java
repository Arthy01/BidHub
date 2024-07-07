package de.hwrberlin.bidhub.json.dataTypes;

import de.hwrberlin.bidhub.model.shared.ApplicationClient;

/**
 * Das ProductInfo-Record speichert die Informationen zu einem Produkt.
 *
 * @param title der Titel des Produkts
 * @param description die Beschreibung des Produkts
 * @param seller der Verkäufer des Produkts
 * @param id die ID des Produkts
 */
public record ProductInfo(String title, String description, ApplicationClient seller, long id) {
}
