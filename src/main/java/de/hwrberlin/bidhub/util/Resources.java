package de.hwrberlin.bidhub.util;

import de.hwrberlin.bidhub.ClientApplication;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public abstract class Resources {
    public static URL getURL(String relativePath){
        return Objects.requireNonNull(ClientApplication.class.getResource(relativePath));
    }

    public static InputStream getURLAsStream(String relativePath){
        return Objects.requireNonNull(ClientApplication.class.getResourceAsStream(relativePath));
    }
}
