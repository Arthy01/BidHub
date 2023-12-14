package de.hwrberlin.bidhub.util;

import de.hwrberlin.bidhub.ClientApplication;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Abstract class managing stages and popups in the application.
 * Provides methods for initializing the main stage, creating and managing popups,
 * setting scenes, creating new stages, and setting titles for stages.
 */
public abstract class StageManager {
    private static final String BIDHUB_ICON = "icons/BidHub_Icon.png";
    private static Stage mainStage;
    private static final ArrayList<Stage> openPopups = new ArrayList<>();

    public static void initialize(Stage startStage) {
        mainStage = startStage;

        mainStage.setOnCloseRequest(ClientApplication::handleCloseRequest);

        setScene(mainStage, FxmlFile.Login);

        mainStage.getIcons().add(new Image(Resources.getURLAsStream(BIDHUB_ICON)));
        mainStage.show();
    }

    /**
     * Sets the scene of a stage using the provided FXML file.
     *
     * @param stage The stage for which to set the scene.
     * @param sceneFxmlFile The FxmlFile enum representing the desired scene.
     * @param <T> The type of the controller associated with the scene.
     * @return The controller instance associated with the scene.
     */
    public static <T> T setScene(Stage stage, FxmlFile sceneFxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FxmlRef.Get(sceneFxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Closes all open popups.
     */
    public static void closeAllPopups() {
        Platform.runLater(() -> {
            for (Stage popup : openPopups) {
                popup.close();
            }
            openPopups.clear();
        });
    }

    /**
     * Creates a new stage with the specified scene and title, closing the main stage.
     *
     * @param startingScene The FxmlFile enum representing the starting scene for the new stage.
     * @param stageTitle The title of the new stage.
     */
    public static void createStage(FxmlFile startingScene, String stageTitle) {
        Stage stage = new Stage();
        setScene(stage, startingScene);
        stage.setTitle(stageTitle);
        stage.getIcons().add(new Image(Resources.getURLAsStream(BIDHUB_ICON)));

        mainStage.close();

        mainStage = stage;

        mainStage.setOnCloseRequest(ClientApplication::handleCloseRequest);
        mainStage.show();
    }

    /**
     * Sets the title of the main stage.
     *
     * @param title The new title for the main stage.
     */
    public static void setTitle(String title) {
        mainStage.setTitle("BidHub - " + title);
    }

}
