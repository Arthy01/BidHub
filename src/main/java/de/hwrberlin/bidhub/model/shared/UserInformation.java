package de.hwrberlin.bidhub.model.shared;

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
