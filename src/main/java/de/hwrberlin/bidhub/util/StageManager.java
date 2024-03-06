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
 * Abstract class managing stages and popups in the application.
 * Provides methods for initializing the main stage, creating and managing popups,
 * setting scenes, creating new stages, and setting titles for stages.
 */
public abstract class StageManager {
    private static final String BIDHUB_ICON = "icons/BidHub_Icon.png";
    private static Stage mainStage;

    public static void initialize(Stage startStage, String title) {
        mainStage = startStage;

        mainStage.setOnCloseRequest(ClientApplication::handleCloseRequest);

        setScene(mainStage, FxmlFile.Login, false);

        mainStage.getIcons().add(new Image(Resources.getURLAsStream(BIDHUB_ICON)));

        setTitle(title);

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
    public static <T> T setScene(Stage stage, FxmlFile sceneFxmlFile, boolean resizable) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FxmlRef.Get(sceneFxmlFile));
            Scene scene = new Scene(fxmlLoader.load());

            double prevWidth = stage.getWidth();
            double prevHeight = stage.getHeight();

            stage.setScene(scene);

            stage.setResizable(resizable);

            stage.setMinHeight(scene.getRoot().minHeight(-1));
            stage.setMinWidth(scene.getRoot().minWidth(-1));

            if (prevWidth >= scene.getRoot().minWidth(-1))
                stage.setWidth(prevWidth);

            if (prevHeight >= scene.getRoot().minHeight(-1))
                stage.setHeight(prevHeight);

            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T setScene(FxmlFile sceneFxmlFile, boolean resizable) {
        return setScene(mainStage, sceneFxmlFile, resizable);
    }


    /**
     * Creates a new stage with the specified scene and title, closing the main stage.
     *
     * @param startingScene The FxmlFile enum representing the starting scene for the new stage.
     * @param stageTitle The title of the new stage.
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

    public static <T> T createPopup(FxmlFile scene, String title){
        Stage stage = new Stage();

        T controller = setScene(stage, scene, false);

        stage.setTitle("[Popup] BidHub - " + title);
        stage.getIcons().add(new Image(Resources.getURLAsStream(BIDHUB_ICON)));

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(mainStage);
        stage.showAndWait();

        return controller;
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
