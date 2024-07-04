package de.hwrberlin.bidhub.util;

import de.hwrberlin.bidhub.ClientApplication;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

/**
 * Abstrakte Klasse zur Verwaltung von Stages und Popups in der Anwendung.
 * Bietet Methoden zur Initialisierung der Hauptbühne, Erstellung und Verwaltung von Popups,
 * Festlegen von Szenen, Erstellen neuer Stages und Festlegen von Titeln für Stages.
 */
public abstract class StageManager {
    private static final String BIDHUB_ICON = "icons/BidHub_Icon.png";
    private static Stage mainStage;

    /**
     * Initialisiert die Hauptbühne der Anwendung mit einer gegebenen Szene und Titel.
     * Setzt auch den CloseRequest-Handler, der bei Schließanfragen aufgerufen wird.
     *
     * @param startStage Die Startbühne der Anwendung.
     * @param title Der Titel der Startbühne.
     */
    public static void initialize(Stage startStage, String title) {
        mainStage = startStage;

        mainStage.setOnCloseRequest(ClientApplication::handleCloseRequest);

        setScene(mainStage, FxmlFile.Login, false);

        mainStage.getIcons().add(new Image(Resources.getURLAsStream(BIDHUB_ICON)));

        setTitle(title);

        mainStage.show();
    }

    /**
     * Setzt die Szene einer Bühne unter Verwendung der bereitgestellten FXML-Datei.
     * Ermöglicht das Festlegen, ob die Bühne in der Größe veränderbar sein soll.
     *
     * @param stage Die Bühne, für die die Szene gesetzt wird.
     * @param sceneFxmlFile Die FxmlFile-Enum, die die gewünschte Szene repräsentiert.
     * @param resizable Gibt an, ob die Bühne in der Größe veränderbar sein soll.
     * @param <T> Der Typ des Controllers, der mit der Szene verbunden ist.
     * @return Die Controller-Instanz, die mit der Szene verbunden ist.
     */
    public static <T> T setScene(Stage stage, FxmlFile sceneFxmlFile, boolean resizable) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FxmlRef.Get(sceneFxmlFile));
            Scene scene = new Scene(fxmlLoader.load());

            double prevWidth = stage.getWidth();
            double prevHeight = stage.getHeight();

            stage.setScene(scene);

            stage.setResizable(resizable);

            stage.setMinHeight(scene.getRoot().minHeight(-1) + 0);
            stage.setMinWidth(scene.getRoot().minWidth(-1) + 0);

            if (prevWidth >= scene.getRoot().minWidth(-1))
                stage.setWidth(prevWidth);

            if (prevHeight >= scene.getRoot().minHeight(-1))
                stage.setHeight(prevHeight);

            if (stage.isShowing()){
                stage.hide();
                stage.show();
            }

            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Setzt die Szene der Hauptbühne unter Verwendung der bereitgestellten FXML-Datei.
     * Ermöglicht das Festlegen, ob die Bühne in der Größe veränderbar sein soll.
     *
     * @param sceneFxmlFile Die FxmlFile-Enum, die die gewünschte Szene repräsentiert.
     * @param resizable Gibt an, ob die Bühne in der Größe veränderbar sein soll.
     * @param <T> Der Typ des Controllers, der mit der Szene verbunden ist.
     * @return Die Controller-Instanz, die mit der Szene verbunden ist.
     */
    public static <T> T setScene(FxmlFile sceneFxmlFile, boolean resizable) {
        return setScene(mainStage, sceneFxmlFile, resizable);
    }


    /**
     * Erstellt eine neue Bühne mit der angegebenen Szene und dem Titel und schließt die Hauptbühne.
     *
     * @param startingScene Die FxmlFile-Enum, die die Startszene für die neue Bühne repräsentiert.
     * @param stageTitle Der Titel der neuen Bühne.
     * @param resizable Gibt an, ob die neue Bühne in der Größe veränderbar sein soll.
     */
    public static void createStage(FxmlFile startingScene, String stageTitle, boolean resizable) {
        Stage stage = new Stage();

        setScene(stage, startingScene, resizable);

        stage.setTitle("BidHub - " + stageTitle);
        stage.getIcons().add(new Image(Resources.getURLAsStream(BIDHUB_ICON)));

        mainStage.close();
        mainStage = stage;
        mainStage.setOnCloseRequest(ClientApplication::handleCloseRequest);
        mainStage.show();
    }

    /**
     * Erstellt ein neues Popup mit der angegebenen Szene und dem Titel.
     * Das Popup wird modal in Bezug auf die Hauptbühne angezeigt.
     *
     * @param scene Die FxmlFile-Enum, die die Szene für das Popup repräsentiert.
     * @param title Der Titel des Popups.
     * @param <T> Der Typ des Controllers, der mit der Szene des Popups verbunden ist.
     * @return Ein Paar aus dem Controller und der Bühne des Popups.
     */
    public static <T> Pair<T, Stage> createPopup(FxmlFile scene, String title){
        Stage stage = new Stage();

        T controller = setScene(stage, scene, false);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(mainStage);

        stage.setTitle("[Popup] BidHub - " + title);
        stage.getIcons().add(new Image(Resources.getURLAsStream(BIDHUB_ICON)));

        stage.show();

        return new Pair<>(controller, stage);
    }

    /**
     * Setzt den Titel der Hauptbühne.
     *
     * @param title Der neue Titel für die Hauptbühne.
     */
    public static void setTitle(String title) {
        mainStage.setTitle("BidHub - " + title);
    }
}
