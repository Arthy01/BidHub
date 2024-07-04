package de.hwrberlin.bidhub.model.shared;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Repräsentiert die Informationen eines Auktionsraums.
 * Enthält Details wie ID, Titel, Beschreibung, Passwort und eine Liste der Clients.
 */
public class AuctionRoomInfo implements Serializable {
    private String id;
    private String title;
    private String description;
    private String password;
    private final HashMap<String, Boolean> clients = new HashMap<>();

    /**
     * Erstellt eine neue Instanz von AuctionRoomInfo mit spezifischen Details.
     *
     * @param id Die eindeutige ID des Auktionsraums.
     * @param title Der Titel des Auktionsraums.
     * @param description Die Beschreibung des Auktionsraums.
     * @param password Das Passwort für den Zugang zum Auktionsraum.
     */
    public AuctionRoomInfo(String id, String title, String description, String password) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.password = password;
    }

    /**
     * Fügt einen Client zur Liste der Clients im Auktionsraum hinzu.
     *
     * @param username Der Benutzername des Clients.
     * @param isInitiator Gibt an, ob der Client der Initiator des Auktionsraums ist.
     */
    public void addClient(String username, Boolean isInitiator){
        clients.put(username, isInitiator);
    }

    /**
     * Entfernt einen Client aus der Liste der Clients im Auktionsraum.
     *
     * @param username Der Benutzername des zu entfernenden Clients.
     */
    public void removeClient(String username){
        clients.remove(username);
    }

    /**
     * Gibt die Liste der Clients im Auktionsraum zurück.
     *
     * @return Eine HashMap mit den Benutzernamen der Clients und einem Boolean, der angibt, ob der Client der Initiator ist.
     */
    public HashMap<String, Boolean> getClients(){
        return clients;
    }

    /**
     * Gibt die ID des Auktionsraums zurück.
     *
     * @return Die ID des Auktionsraums.
     */
    public String getId() {
        return id;
    }

    /**
     * Setzt die ID des Auktionsraums.
     *
     * @param id Die neue ID des Auktionsraums.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gibt den Titel des Auktionsraums zurück.
     *
     * @return Der Titel des Auktionsraums.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setzt den Titel des Auktionsraums.
     *
     * @param title Der neue Titel des Auktionsraums.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gibt die Beschreibung des Auktionsraums zurück.
     *
     * @return Die Beschreibung des Auktionsraums.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setzt die Beschreibung des Auktionsraums.
     *
     * @param description Die neue Beschreibung des Auktionsraums.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gibt die aktuelle Anzahl der Clients im Auktionsraum zurück.
     *
     * @return Die Anzahl der Clients im Auktionsraum.
     */
    public int getCurrentClients() {
        return clients.size();
    }

    /**
     * Gibt das Passwort des Auktionsraums zurück.
     *
     * @return Das Passwort des Auktionsraums.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setzt das Passwort des Auktionsraums.
     *
     * @param hashedPassword Das neue Passwort des Auktionsraums.
     */
    public void setPassword(String hashedPassword) {
        this.password = hashedPassword;
    }
}
