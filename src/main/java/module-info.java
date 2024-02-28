module de.hwrberlin.bidhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;


    opens de.hwrberlin.bidhub to javafx.fxml;
    opens de.hwrberlin.bidhub.controller to javafx.fxml;

    exports de.hwrberlin.bidhub;
    //exports de.hwrberlin.bidhub.model.client;
    exports de.hwrberlin.bidhub.model.shared;
}