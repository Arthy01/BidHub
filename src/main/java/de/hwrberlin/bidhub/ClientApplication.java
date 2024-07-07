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

/**
 * Die ClientApplication ist der Haupteinstiegspunkt für die Client-Anwendung. Sie verwaltet die Verbindung
 * zum Server, das Anwendungsfenster und die Benutzerinformationen.
 */
public class ClientApplication extends Application {
    private static boolean closedByCloseRequest = false;
    private static final ArrayList<Runnable> closeRequestHooks = new ArrayList<>();
    private static ClientSocketManager clientSocketManager;
    private static ApplicationClient applicationClient;

    /**
     * Fügt einen Hook hinzu, der beim Schließen der Anwendung ausgeführt wird.
     *
     * @param runnable der Hook, der beim Schließen ausgeführt wird
     */
    public static void addCloseRequestHook(Runnable runnable){
        closeRequestHooks.add(runnable);
    }

    /**
     * Entfernt einen zuvor hinzugefügten Hook.
     *
     * @param runnable der Hook, der entfernt werden soll
     */
    public static void removeCloseRequestHook(Runnable runnable){
        closeRequestHooks.remove(runnable);
    }

    /**
     * Setzt den aktuellen ApplicationClient.
     *
     * @param applicationClient der ApplicationClient, der gesetzt werden soll
     */
    public static void setApplicationClient(ApplicationClient applicationClient){
        ClientApplication.applicationClient = applicationClient;
    }

    /**
     * Gibt den aktuellen ApplicationClient zurück.
     *
     * @return der aktuelle ApplicationClient
     */
    public static ApplicationClient getApplicationClient(){
        return applicationClient;
    }

    /**
     * Hauptmethode der Anwendung. Initialisiert die Verbindung und startet die JavaFX-Anwendung.
     *
     * @param args die Befehlszeilenargumente
     */
    public static void main(String[] args) {
        System.out.println(Helpers.hashPassword("test"));
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

    /**
     * Gibt den ClientSocketManager zurück.
     *
     * @return der ClientSocketManager
     */
    public static ClientSocketManager getSocketManager(){
        return clientSocketManager;
    }

    /**
     * Startet die JavaFX-Anwendung und zeigt das Hauptfenster an.
     *
     * @param stage die Hauptbühne der Anwendung
     */
    @Override
    public void start(Stage stage) {
        StageManager.initialize(stage, "Login");
    }

    /**
     * Führt die Abmeldung des aktuellen Benutzers durch.
     */
    public static void logout(){
        for (Runnable runnable : closeRequestHooks){
            runnable.run();
        }
        closeRequestHooks.clear();

        unregisterFromCurrentConnectedRoom();
        StageManager.createStage(FxmlFile.Login, "Login", false);
        applicationClient = null;
    }

    /**
     * Wird aufgerufen, wenn eine Schließanforderung für das Anwendungsfenster erfolgt.
     *
     * @param event das WindowEvent, das die Schließanforderung auslöst
     */
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

    /**
     * Behandelt den Shutdown-Hook der Anwendung, z.B. wenn die Anwendung abstürzt.
     */
    private static void handleShutdownHook(){
        if (!closedByCloseRequest){
            System.err.println("Application stopped unexpectedly!");
            handleCloseRequest(null);
        }
    }

    /**
     * Meldet den aktuellen Benutzer vom aktuell verbundenen Raum ab.
     */
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
