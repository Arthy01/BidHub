package de.hwrberlin.bidhub.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
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
            put(FxmlFile.AuctionRoom, "fxml/AuctionRoom.fxml");
            put(FxmlFile.ChatMessage, "fxml/ChatMessage.fxml");
            put(FxmlFile.Overview, "fxml/DashboardBodies/Overview.fxml");
            put(FxmlFile.JoinAuction, "fxml/DashboardBodies/JoinAuction.fxml");
            put(FxmlFile.AuctionRoomPreview, "fxml/AuctionRoomPreview.fxml");
            put(FxmlFile.CreateAuction, "fxml/DashboardBodies/CreateAuction.fxml");
            put(FxmlFile.StartAuctionPopup, "fxml/StartAuctionPopup.fxml");
            put(FxmlFile.InfoPopup, "fxml/InfoPopup.fxml");
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

    public static <T> Pair<Node, T> GetInstance(FxmlFile fxml) {
        FXMLLoader loader = new FXMLLoader(Get(fxml));
        try {
            Node node = loader.load();
            T controller = loader.getController();
            return new Pair<>(node, controller);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
