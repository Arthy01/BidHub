package de.hwrberlin.bidhub.util;

import de.hwrberlin.bidhub.ClientApplication;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Bietet Hilfsmethoden zum Zugriff auf Ressourcen wie Bilder und FXML-Dateien innerhalb der Anwendung.
 */
public abstract class Resources {
    /**
     * Gibt die URL einer Ressource relativ zum Klassenpfad zurück.
     *
     * @param relativePath Der relative Pfad der Ressource.
     * @return Die URL der angeforderten Ressource.
     * @throws NullPointerException wenn die Ressource nicht gefunden wird.
     */
    public static URL getURL(String relativePath){
        return Objects.requireNonNull(ClientApplication.class.getResource(relativePath));
    }

    /**
     * Gibt einen InputStream einer Ressource relativ zum Klassenpfad zurück.
     *
     * @param relativePath Der relative Pfad der Ressource.
     * @return Ein InputStream der angeforderten Ressource.
     * @throws NullPointerException wenn die Ressource nicht gefunden wird.
     */
    public static InputStream getURLAsStream(String relativePath){
        return Objects.requireNonNull(ClientApplication.class.getResourceAsStream(relativePath));
    }
}
