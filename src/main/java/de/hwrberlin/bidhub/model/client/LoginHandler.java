package de.hwrberlin.bidhub.model.client;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.RMIInfo;
import de.hwrberlin.bidhub.model.shared.ILoginService;
import de.hwrberlin.bidhub.util.FxmlFile;
import de.hwrberlin.bidhub.util.StageManager;
import javafx.application.Platform;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginHandler {
    private ILoginService loginService;

    public LoginHandler() throws NotBoundException, RemoteException {
        locateServices();
    }

    private void locateServices() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(RMIInfo.getHost(), RMIInfo.getPort());
        loginService = (ILoginService) registry.lookup("LoginService");
    }

    public void login(String username, String password){
        ClientApplication.executor.submit(() -> {
            try {
                boolean isLoginCorrect = loginService.requestLogin(username, password);
                if (isLoginCorrect){
                    Platform.runLater(() -> onSuccessfulLogin(username)); // Auf dem Main Thread aufrufen
                }
                else {
                    Platform.runLater(this::onFailedLogin); // Auf dem Main Thread aufrufen
                }
            }
            catch (RemoteException e) {
                Platform.runLater(() -> {
                    ClientApplication.logout();
                    throw new RuntimeException(e);
                });
            }
        });
    }

    private void onSuccessfulLogin(String username) {
        System.out.println("Success!");
        ClientApplication.setApplicationClient(new ApplicationClient(username));
        StageManager.createStage(FxmlFile.Dashboard, "Dashboard", true);
    }

    private void onFailedLogin(){
        System.out.println("Fail!");
    }
}
