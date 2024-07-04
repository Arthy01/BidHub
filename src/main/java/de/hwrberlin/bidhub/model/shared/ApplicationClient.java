package de.hwrberlin.bidhub.model.shared;

/**
 * Repräsentiert einen Anwendungsklienten mit Benutzerinformationen.
 * Enthält Informationen wie Benutzername, ID, E-Mail, Vorname, Nachname und IBAN.
 */
public class ApplicationClient {
    private final String username;
    private final long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String iban;
    private String currentConnectedRoom = "";

    /**
     * Erstellt eine neue Instanz eines ApplicationClient.
     *
     * @param id Die eindeutige ID des Benutzers.
     * @param username Der Benutzername des Klienten.
     * @param email Die E-Mail-Adresse des Klienten.
     * @param firstName Der Vorname des Klienten.
     * @param lastName Der Nachname des Klienten.
     * @param iban Die IBAN des Klienten.
     */
    public ApplicationClient(long id, String username, String email, String firstName, String lastName, String iban){
        this.username = username;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.iban = iban;
    }

    /**
     * Gibt den Benutzernamen des Klienten zurück.
     *
     * @return Der Benutzername des Klienten.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gibt den Namen des aktuell verbundenen Raums zurück.
     *
     * @return Der Name des aktuell verbundenen Raums.
     */
    public String getCurrentConnectedRoom() {
        return currentConnectedRoom;
    }

    /**
     * Setzt den Namen des aktuell verbundenen Raums.
     *
     * @param currentConnectedRoom Der Name des Raums, mit dem der Klient aktuell verbunden ist.
     */
    public void setCurrentConnectedRoom(String currentConnectedRoom) {
        this.currentConnectedRoom = currentConnectedRoom;
    }

    /**
     * Gibt die ID des Klienten zurück.
     *
     * @return Die ID des Klienten.
     */
    public long getId() {
        return id;
    }

    /**
     * Gibt den Vornamen des Klienten zurück.
     *
     * @return Der Vorname des Klienten.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gibt den Nachnamen des Klienten zurück.
     *
     * @return Der Nachname des Klienten.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gibt die IBAN des Klienten zurück.
     *
     * @return Die IBAN des Klienten.
     */
    public String getIban() {
        return iban;
    }

    /**
     * Gibt die E-Mail-Adresse des Klienten zurück.
     *
     * @return Die E-Mail-Adresse des Klienten.
     */
    public String getEmail() {
        return email;
    }
}
