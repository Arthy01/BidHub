package de.hwrberlin.bidhub.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Abstrakte Klasse, die Referenzen zu verschiedenen FXML-Dateipfaden in der Anwendung bereitstellt.
 * Enthält eine Zuordnung von FxmlFile-Enum-Werten zu ihren entsprechenden FXML-Dateipfaden.
 */
public abstract class FxmlRef {
    private static final HashMap<FxmlFile, String> paths = new HashMap<>() {
        {
            put(FxmlFile.Login, "fxml/Login.fxml");
            put(FxmlFile.Register, "fxml/Register.fxml");
            put(FxmlFile.Dashboard, "fxml/Dashboard.fxml");
            put(FxmlFile.AuctionRoom, "fxml/AuctionRoom.fxml");
            put(FxmlFile.ChatMessage, "fxml/ChatMessage.fxml");
            put(FxmlFile.Overview, "fxml/DashboardBodies/Overview.fxml");
            put(FxmlFile.JoinAuction, "fxml/DashboardBodies/JoinAuction.fxml");
            put(FxmlFile.AuctionRoomPreview, "fxml/AuctionRoomPreview.fxml");
            put(FxmlFile.CreateAuction, "fxml/DashboardBodies/CreateAuction.fxml");
            put(FxmlFile.StartAuctionPopup, "fxml/StartAuctionPopup.fxml");
            put(FxmlFile.InfoPopup, "fxml/InfoPopup.fxml");
            put(FxmlFile.UserInformation, "fxml/UserInformation.fxml");
            put(FxmlFile.RoomInformation, "fxml/RoomInformation.fxml");
        }
    };

    /**
     * Abstrakte Klasse, die Referenzen zu verschiedenen FXML-Dateipfaden in der Anwendung bereitstellt.
     * Enthält eine Zuordnung von FxmlFile-Enum-Werten zu ihren entsprechenden FXML-Dateipfaden.
     */
    public static URL Get(FxmlFile fxml) {
        return Resources.getURL(paths.get(fxml));
    }

    /**
     * Erstellt eine Instanz des FXML-Controllers und des zugehörigen Nodes basierend auf dem gegebenen FxmlFile-Enum-Wert.
     * Diese Methode lädt die FXML-Datei, instanziiert den Controller und gibt beides in einem Pair zurück.
     *
     * @param fxml Der FxmlFile-Enum-Wert, der die gewünschte FXML-Datei repräsentiert.
     * @return Ein Pair, das den geladenen Node und den Controller enthält. Gibt {@code null} zurück, wenn ein Fehler auftritt.
     */
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
