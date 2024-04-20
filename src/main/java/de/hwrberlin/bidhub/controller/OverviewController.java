package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.model.client.Transactions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OverviewController {

    @FXML
    private TableView<String> kaufeTable;

    @FXML
    private TableView<String> verkaufeTable;

    @FXML
    public void initialize() {
        adjustColumnWidths(kaufeTable);
        adjustColumnWidths(verkaufeTable);
    /*
        kaufeTable.getItems().addAll(
                new Transactions("Laptop", "1200€", "Alice"),
                new Transactions("Smartphone", "800€", "Bob")
        );
        verkaufeTable.getItems().addAll(
                new Transactions("Fahrrad", "150€", "Charlie"),
                new Transactions("Kaffeemaschine", "90€", "Dana")
        );
    */

    }

    public void fillUserInformation() {
        String testUsername = "test";
        String testIBAN = "test_iban";
        ObservableList<String> test = FXCollections.observableArrayList("test");
        //kaufeTable.setItems(test);
    }


    private void adjustColumnWidths(TableView<?> tableView) {
        ObservableList<? extends TableColumn<?, ?>> columns = tableView.getColumns();
        columns.forEach(column -> column.prefWidthProperty().bind(tableView.widthProperty().divide(columns.size())));
    }
}
