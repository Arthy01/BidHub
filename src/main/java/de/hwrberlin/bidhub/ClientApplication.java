package de.hwrberlin.bidhub;


import de.hwrberlin.bidhub.model.client.Client;
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
    private static Client client;
    private static boolean closedByCloseRequest = false;

    public static void createClient(String username) throws RemoteException{
        if (client == null)
            client = new Client(username);
    }

    public static Client getClient(){
        return client;
    }

    public static void deleteClient(){
        try {
            UnicastRemoteObject.unexportObject(client, true);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        client = null;
    }

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
        deleteClient();
    }

    public static void handleCloseRequest(WindowEvent event) {
        System.out.println("Handle Close Request");

        deleteClient();
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
