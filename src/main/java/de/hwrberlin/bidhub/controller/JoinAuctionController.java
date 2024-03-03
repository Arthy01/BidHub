package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.model.client.JoinAuctionHandler;
import de.hwrberlin.bidhub.model.shared.AuctionRoomInfo;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.FxmlRef;
import de.hwrberlin.bidhub.util.Pair;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class JoinAuctionController implements Initializable {
    @FXML
    private VBox fxScrollPaneContent;
    @FXML
    private TextField fxPrivateRoomId;
    @FXML
    private TextField fxPrivateRoomPassword;
    @FXML
    private Button fxJoinPrivateRoom;

    private JoinAuctionHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            handler = new JoinAuctionHandler();
        }
        catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
            return;
        }

        createPreviews();

        fxJoinPrivateRoom.setOnAction(e -> handler.joinAuction(fxPrivateRoomId.getText(), fxPrivateRoomPassword.getText()));
    }

    private void createPreviews(){
        ArrayList<AuctionRoomInfo> infos;

        try{
            infos = handler.getPreviews();
        }
        catch (RemoteException e){
            ClientApplication.logout();
            e.printStackTrace();
            return;
        }

        for (AuctionRoomInfo info : infos){
            createPreview(info);
        }
    }

    private void createPreview(AuctionRoomInfo info){
        Pair<Node, AuctionRoomPreviewController> instance = FxmlRef.GetInstance(FxmlFile.AuctionRoomPreview);
        instance.getValue().initialize(info, () -> handler.joinAuction(info.getId(), ""));
        fxScrollPaneContent.getChildren().add(instance.getKey());
    }
}
