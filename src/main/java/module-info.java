module de.hwrberlin.bidhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.java_websocket;
    requires com.google.gson;

    opens de.hwrberlin.bidhub to javafx.fxml;
    opens de.hwrberlin.bidhub.controller to javafx.fxml;

    exports de.hwrberlin.bidhub;
    exports de.hwrberlin.bidhub.model.client;
    exports de.hwrberlin.bidhub.model.shared;
    exports de.hwrberlin.bidhub.util;
    exports de.hwrberlin.bidhub.json;
    exports de.hwrberlin.bidhub.json.dataTypes;
}