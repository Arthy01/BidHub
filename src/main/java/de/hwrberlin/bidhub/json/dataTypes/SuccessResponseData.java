package de.hwrberlin.bidhub.json.dataTypes;

/**
 * Das SuccessResponseData-Record speichert die Antwortdaten für eine erfolgreiche oder fehlgeschlagene Anfrage.
 *
 * @param success gibt an, ob die Anfrage erfolgreich war
 */
public record SuccessResponseData(boolean success) {
}
