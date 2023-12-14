module de.hwrberlin.bidhub.bidhub {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.hwrberlin.bidhub.bidhub to javafx.fxml;
    exports de.hwrberlin.bidhub.bidhub;
}