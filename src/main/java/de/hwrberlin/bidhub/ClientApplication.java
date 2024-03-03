package de.hwrberlin.bidhub;


import de.hwrberlin.bidhub.model.client.ApplicationClient;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApplication extends Application {
    public static final ExecutorService executor = Executors.newCachedThreadPool();
    private static boolean closedByCloseRequest = false;
    private static final ArrayList<Remote> exportedObjects = new ArrayList<>();
    private static final ArrayList<Runnable> closeRequestHooks = new ArrayList<>();

    private static ApplicationClient applicationClient;

    public static void setRmiHostName() {
        try {
            for (NetworkInterface netInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {

                if (!netInterface.isUp() || netInterface.isLoopback() || !netInterface.getDisplayName().toLowerCase().contains("hamachi")) continue;

                for (InetAddress inetAddress : Collections.list(netInterface.getInetAddresses())) {

                    if (inetAddress instanceof Inet4Address) {
                        System.out.println("Erfolgreich mit VPN (Hamachi) verbunden");
                        System.setProperty("java.rmi.server.hostname", inetAddress.getHostAddress());
                        return;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("VPN (Hamachi) Ipv4 Adresse nicht gefunden! Versuche mit Ip.");
        setRmiHostNameByIpStart();
    }

    public static void setRmiHostNameByIpStart(){
        try {
            for (NetworkInterface netInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(netInterface.getInetAddresses())) {

                    if (inetAddress instanceof Inet4Address && inetAddress.getHostAddress().startsWith("25.")) {
                        System.out.println("Erfolgreich mit VPN (Hamachi) verbunden");
                        System.setProperty("java.rmi.server.hostname", inetAddress.getHostAddress());
                        return;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("VPN (Hamachi) Ipv4 Adresse nicht gefunden! (By Ip)");
    }

    public static void addExportedObject(Remote obj){
        exportedObjects.add(obj);
    }

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
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "750");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "750");
        System.setProperty("sun.rmi.transport.connectionTimeout", "750");
        System.setProperty("sun.rmi.transport.proxy.connectTimeout", "750");
        System.setProperty("sun.rmi.transport.tcp.handshakeTimeout", "750");
        setRmiHostName();

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

        StageManager.createStage(FxmlFile.Login, "Login", false, false);
        applicationClient = null;
    }

    public static void handleCloseRequest(WindowEvent event) {
        System.out.println("Handle Close Request");

        for (Runnable runnable : closeRequestHooks){
            runnable.run();
        }

        closeRequestHooks.clear();

        executor.shutdownNow();

        for (Remote remote : exportedObjects){
            try{
                UnicastRemoteObject.unexportObject(Objects.requireNonNull(remote), true);
            }
            catch (NoSuchObjectException | NullPointerException e){}
        }

        closedByCloseRequest = true;
    }

    private static void handleShutdownHook(){
        if (!closedByCloseRequest){
            System.err.println("Application stopped unexpectedly!");
            handleCloseRequest(null);
        }
    }
}
