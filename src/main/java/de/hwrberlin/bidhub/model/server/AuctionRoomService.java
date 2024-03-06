package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.ServerApplication;
import de.hwrberlin.bidhub.model.shared.AuctionRoomClientData;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomClient;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomService;
import de.hwrberlin.bidhub.util.Helpers;
import de.hwrberlin.bidhub.util.TimeoutNetworkAction;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuctionRoomService extends UnicastRemoteObject implements IAuctionRoomService, Runnable {
    private final HashMap<IAuctionRoomClient, AuctionRoomClientData> clients = new HashMap<>();
    private final Registry registry;
    private volatile boolean running = true;
    private final AuctionRoomInfo info;
    private final ArrayList<Runnable> closeRoomHooks = new ArrayList<>();

    protected AuctionRoomService(Registry registry, AuctionRoomInfo info) throws RemoteException {
        super();

        this.registry = registry;
        this.info = info;
    }

    public void addCloseRoomHook(Runnable runnable){
        closeRoomHooks.add(runnable);
    }

    public void removeCloseRoomHook(Runnable runnable){
        closeRoomHooks.remove(runnable);
    }

    @Override
    public void closeRoom() throws RemoteException {
        for (Map.Entry<IAuctionRoomClient, AuctionRoomClientData> entry : clients.entrySet()){
            if (entry.getValue().isInitiator())
                continue;

            TimeoutNetworkAction.execute(() -> {
                try {
                    entry.getKey().kick("Der Raum wurde geschlossen!");
                } catch (RemoteException ignored) {}
            },
            null,
            ServerApplication.TIMEOUT_TIME);
        }

        UnicastRemoteObject.unexportObject(this, true);

        try {
            registry.unbind(info.getId());
        }
        catch (NotBoundException e){
            System.out.println("Es wurde versucht die Registry " + info.getId() + " zu unbinden. Diese existiert nicht.");
        }

        running = false;
    }

    @Override
    public AuctionRoomInfo getRoomInfo() throws RemoteException{
        return info;
    }

    @Override
    public void registerClient(IAuctionRoomClient client, AuctionRoomClientData clientData) throws RemoteException {
        System.out.println("Register client: " + clientData.username());
        clients.put(client, clientData);
        invokeClientStateChange();
        sendSystemMessage(clientData.username() + " ist der Auktion beigetreten.");
        info.addClient(clientData.username(), clientData.isInitiator());
    }

    @Override
    public void unregisterClient(IAuctionRoomClient client, AuctionRoomClientData clientData) throws RemoteException {
        if (!clients.containsKey(client)){
            System.out.println("Client " + clientData.username() + "not registred anymore, so it cant be unregistered again.");
            return;
        }

        System.out.println("Unregister client: " + clientData.username());
        clients.remove(client);
        invokeClientStateChange();
        sendSystemMessage(clientData.username() + " hat die Auktion verlassen.");
        info.removeClient(clientData.username());
    }

    private void invokeClientStateChange(){
        for (IAuctionRoomClient client : clients.keySet()){
            TimeoutNetworkAction.execute(
                    () -> {
                        try {
                            client.clientStateChange();
                        }
                        catch (RemoteException ignored) {}
                    },
                    () -> {
                        System.err.println("Fehler: Client " + clients.get(client).username() + " ist nicht erreichbar.");
                        try {
                            unregisterClient(client, clients.get(client));
                        }
                        catch (RemoteException e) {e.printStackTrace();}
                    },
                    ServerApplication.TIMEOUT_TIME);
        }
    }

    @Override
    public void sendMessage(IAuctionRoomClient sender, String message) throws RemoteException {
        String finalMessage = "(" + getCurrentTime() + ") " + "[" + clients.get(sender).username() + "] " + message;
        System.out.println("Room [" + info.getId() + "] : " + finalMessage);

        boolean important = clients.get(sender).isInitiator();

        for (IAuctionRoomClient client : clients.keySet()){
            TimeoutNetworkAction.execute(
                    () -> {
                        try {
                            client.receiveMessage(finalMessage, important);
                        }
                        catch (RemoteException ignored) {}
                    },
                    () -> {
                        System.err.println("Fehler: Client " + clients.get(client).username() + " ist nicht erreichbar.");
                        try {
                            unregisterClient(client, clients.get(client));
                        }
                        catch (RemoteException e) {e.printStackTrace();}
                    },
                    ServerApplication.TIMEOUT_TIME);
        }
    }

    private void sendSystemMessage(String message){
        String finalMessage = "(" + getCurrentTime() + ") " + "[SYSTEM] " + message;

        for (IAuctionRoomClient client : clients.keySet()){
            TimeoutNetworkAction.execute(() ->{
                try {
                    client.receiveMessage(finalMessage, true);
                }catch (RemoteException ignored){}
            },
            null,
            ServerApplication.TIMEOUT_TIME);
        }
    }

    @Override
    public boolean comparePassword(String password) throws RemoteException {
        return password.equals(info.getPassword());
    }

    private String getCurrentTime(){
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }

    @Override
    public void run() {
        System.out.println("Room" + info.getId() + " erstellt");

        while (running){

        }

        for (Runnable runnable : closeRoomHooks){
            runnable.run();
        }

        System.out.println("Auktionsraum " + info.getId() + " wurde geschlossen.");
    }

    public synchronized int getClientCount(){
        return clients.size();
    }
}
