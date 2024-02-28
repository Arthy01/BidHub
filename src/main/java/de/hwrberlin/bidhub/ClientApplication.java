package de.hwrberlin.bidhub;


import de.hwrberlin.bidhub.model.shared.ILoginHandler;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApplication extends Application {
    public static final ExecutorService executor = Executors.newCachedThreadPool();
    private static boolean closedByCloseRequest = false;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(ClientApplication::handleShutdownHook));
        launch();
    }

    @Override
    public void start(Stage stage) {
        StageManager.initialize(stage);
        StageManager.setTitle("Login");
    }

    public static void logout(){
        StageManager.createStage(FxmlFile.Login, "Login");
    }

    public static void handleCloseRequest(WindowEvent event) {
        System.out.println("Handle Close Request");

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
