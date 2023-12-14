module de.hwrberlin.bidhub {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.hwrberlin.bidhub to javafx.fxml;
    exports de.hwrberlin.bidhub;
}