package de.hwrberlin.bidhub;


import de.hwrberlin.bidhub.json.JsonMessage;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import de.hwrberlin.bidhub.model.shared.CallbackType;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ClientApplication extends Application {
    private static boolean closedByCloseRequest = false;
    private static final ArrayList<Runnable> closeRequestHooks = new ArrayList<>();
    private static ClientSocketManager clientSocketManager;
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
        try {
            clientSocketManager = new ClientSocketManager(SocketInfo.getConnectionURI());
            new Thread(() -> clientSocketManager.connect()).start();
        }
        catch (URISyntaxException e){
            System.out.println("Wrong URI!");
            e.printStackTrace();
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(ClientApplication::handleShutdownHook));

        launch();
    }

    public static ClientSocketManager getSocketManager(){
        return clientSocketManager;
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

        unregisterFromCurrentConnectedRoom();
        StageManager.createStage(FxmlFile.Login, "Login", false);
        applicationClient = null;
    }

    public static void handleCloseRequest(WindowEvent event) {
        System.out.println("Handle Close Request");
        for (Runnable runnable : closeRequestHooks){
            runnable.run();
        }

        closeRequestHooks.clear();

        unregisterFromCurrentConnectedRoom();
        clientSocketManager.close();

        closedByCloseRequest = true;
    }

    private static void handleShutdownHook(){
        if (!closedByCloseRequest){
            System.err.println("Application stopped unexpectedly!");
            handleCloseRequest(null);
        }
    }

    public static void unregisterFromCurrentConnectedRoom(){
        if (applicationClient == null)
            return;

        if (applicationClient.getCurrentConnectedRoom().isBlank())
            return;

        JsonMessage msg = new JsonMessage(CallbackType.Server_UnregisterClient.name() + applicationClient.getCurrentConnectedRoom());
        getSocketManager().send(msg);

        applicationClient.setCurrentConnectedRoom("");
    }
}
