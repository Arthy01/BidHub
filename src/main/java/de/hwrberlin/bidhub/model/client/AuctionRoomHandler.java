package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.controller.ChatMessageController;
import de.hwrberlin.bidhub.model.shared.AuctionRoomClientData;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomClient;
import de.hwrberlin.bidhub.model.shared.IAuctionRoomService;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.FxmlRef;
import de.hwrberlin.bidhub.util.Pair;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuctionRoomHandler extends UnicastRemoteObject implements IAuctionRoomClient {
    private final IAuctionRoomService auctionRoomService;
    private final Runnable closeRoomHook;
    private final Runnable unregisterHook;
    private boolean isInitiator = false;
    private final AuctionRoomClientData clientData;
    private final VBox fxChatParent;
    private final ScrollPane fxChatScrollPane;
    private final Runnable onClientStateChange;

    public AuctionRoomHandler(IAuctionRoomService auctionRoomService, boolean isInitiator, VBox fxChatParent, ScrollPane fxChatScrollPane, Runnable onClientStateChange) throws RemoteException {
        super();

        ClientApplication.addExportedObject(this);

        this.auctionRoomService = auctionRoomService;
        this.isInitiator = isInitiator;

        this.fxChatParent = fxChatParent;
        this.fxChatScrollPane = fxChatScrollPane;
        this.onClientStateChange = onClientStateChange;

        clientData = new AuctionRoomClientData(isInitiator,
                ClientApplication.getApplicationClient().getUsername());

        System.out.println("Register self");
        this.auctionRoomService.registerClient(this, clientData);
        System.out.println("Registration successful");

        if (isInitiator) {
            closeRoomHook = () -> {
                try {
                    this.auctionRoomService.closeRoom();
                } catch (RemoteException e) {
                    System.out.println("Could not close room! Server could not be reached!");
                }
            };

            unregisterHook = null;
            ClientApplication.addCloseRequestHook(closeRoomHook);
        }
        else {
            closeRoomHook = null;

            unregisterHook = () -> {
                try {
                    auctionRoomService.unregisterClient(this, clientData);
                }
                catch (RemoteException e) {
                    System.out.println("Could not unregister client!");
                    e.printStackTrace();
                }
            };

            ClientApplication.addCloseRequestHook(unregisterHook);
        }
    }

    public void sendMessage(String message){
        try{
            auctionRoomService.sendMessage(this, message);
        } catch (RemoteException e) {
            ClientApplication.logout();
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(String message, boolean important) throws RemoteException {
        Platform.runLater(() -> {
            System.out.println(message);
            Pair<Node, ChatMessageController> instance = FxmlRef.GetInstance(FxmlFile.ChatMessage);

            instance.getValue().setText(message, important);
            fxChatParent.getChildren().add(instance.getKey());

            Platform.runLater( () -> fxChatScrollPane.setVvalue(1));
        });
    }

    @Override
    public void kick(String kickMessage) throws RemoteException {
        Platform.runLater(() -> {
            onLeaveRoom();
            System.out.println(kickMessage);
        });
    }

    @Override
    public void clientStateChange(){
        onClientStateChange.run();
    }

    public AuctionRoomInfo getRoomInfo(){
        try {
            return auctionRoomService.getRoomInfo();
        }
        catch (RemoteException e) {
            ClientApplication.logout();
            e.printStackTrace();
            return null;
        }
    }

    public void onLeaveRoom(){
        if (isInitiator){
            try {
                auctionRoomService.closeRoom();
                ClientApplication.removeCloseRequestHook(closeRoomHook);
            }
            catch (RemoteException e) {
                ClientApplication.logout();
                throw new RuntimeException(e);
            }
        }
        else
            ClientApplication.removeCloseRequestHook(unregisterHook);

        try {
            auctionRoomService.unregisterClient(this, clientData);
        }
        catch (RemoteException e) {
            System.out.println("Der Auktionsraum existiert nicht mehr, daher kann der client nicht abgemeldet werden.");
        }

        StageManager.setScene(FxmlFile.Dashboard, true);
    }
}
