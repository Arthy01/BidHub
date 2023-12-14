package de.hwrberlin.bidhub.util;

import java.net.URL;
import java.util.HashMap;

/**
 * Abstract class providing references to various FXML file paths used in the application.
 * Contains a mapping of FxmlFile enum values to their corresponding FXML file paths.
 */
public abstract class FxmlRef {
    private static final HashMap<FxmlFile, String> paths = new HashMap<>() {
        {
            put(FxmlFile.Login, "fxml/Login.fxml");
            put(FxmlFile.Dashboard, "fxml/Dashboard.fxml");
        }
    };

    /**
     * Retrieves the FXML file path for the given FxmlFile enum value.
     *
     * @param fxml The FxmlFile enum value representing the desired FXML file.
     * @return The corresponding FXML file path.
     */
    public static URL Get(FxmlFile fxml) {
        return Resources.getURL(paths.get(fxml));
    }
}
