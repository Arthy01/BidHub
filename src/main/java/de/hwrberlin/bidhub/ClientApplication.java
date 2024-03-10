package de.hwrberlin.bidhub;


import de.hwrberlin.bidhub.model.client.ApplicationClient;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApplication extends Application {
    public static final ExecutorService executor = Executors.newCachedThreadPool();
    private static boolean closedByCloseRequest = false;
    private static final ArrayList<Runnable> closeRequestHooks = new ArrayList<>();

    private static ApplicationClient applicationClient;
    public static void addCloseRequestHook(Runnable runnable){
        closeRequestHooks.add(runnable);
    }

    public static void removeCloseRequestHook(Runnable runnable){
        closeRequestHooks.remove(runnable);
    }

    public static void setApplicationClient(ApplicationClient applicationClient){
        ClientApplication.applicationClient = applicationClient;
    }
    public static ApplicationClient getApplicationClient(){
        return applicationClient;
    }

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(ClientApplication::handleShutdownHook));

        launch();
    }

    @Override
    public void start(Stage stage) {
        StageManager.initialize(stage, "Login");
    }

    public static void logout(){
        for (Runnable runnable : closeRequestHooks){
            runnable.run();
        }
        closeRequestHooks.clear();

        StageManager.createStage(FxmlFile.Login, "Login", false);
        applicationClient = null;
    }

    public static void handleCloseRequest(WindowEvent event) {
        System.out.println("Handle Close Request");

        for (Runnable runnable : closeRequestHooks){
            runnable.run();
        }

        closeRequestHooks.clear();

        executor.shutdownNow();

        closedByCloseRequest = true;
    }

    private static void handleShutdownHook(){
        if (!closedByCloseRequest){
            System.err.println("Application stopped unexpectedly!");
            handleCloseRequest(null);
        }
    }
}
