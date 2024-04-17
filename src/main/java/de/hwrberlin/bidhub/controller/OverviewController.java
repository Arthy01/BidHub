package de.hwrberlin.bidhub.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OverviewController {

    @FXML
    private TableView<?> kaufeTable;

    @FXML
    private TableView<?> verkaufeTable;

    @FXML
    public void initialize() {
        // Sorgen Sie dafür, dass die Spalten sich dynamisch auf die gesamte Breite der TableView aufteilen
        adjustColumnWidths(kaufeTable);
        adjustColumnWidths(verkaufeTable);
    }

    private void adjustColumnWidths(TableView<?> tableView) {
        ObservableList<? extends TableColumn<?, ?>> columns = tableView.getColumns();
        columns.forEach(column -> column.prefWidthProperty().bind(tableView.widthProperty().divide(columns.size())));
    }
}
