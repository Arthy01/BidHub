package de.hwrberlin.bidhub.model.shared;

/**
 * Repräsentiert die Benutzerinformationen in der Anwendung.
 * Diese Record-Klasse hält die grundlegenden Informationen eines Benutzers.
 *
 * @param id Die eindeutige ID des Benutzers.
 * @param username Der Benutzername des Benutzers.
 * @param email Die E-Mail-Adresse des Benutzers.
 * @param firstname Der Vorname des Benutzers.
 * @param lastname Der Nachname des Benutzers.
 * @param iban Die IBAN des Benutzers.
 * @param country Das Land, in dem der Benutzer wohnt.
 * @param street Die Straße, in der der Benutzer wohnt.
 * @param streetnumber Die Hausnummer des Benutzers.
 * @param postcode Die Postleitzahl des Wohnorts des Benutzers.
 * @param city Die Stadt, in der der Benutzer wohnt.
 */
public record UserInformation(
        long id,
        String username,
        String email,
        String firstname,
        String lastname,
        String iban,
        String country,
        String street,
        String streetnumber,
        String postcode,
        String city
) {}
