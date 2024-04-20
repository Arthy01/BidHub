package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.dataTypes.TransactionData;
import de.hwrberlin.bidhub.model.client.OverviewModel;
import de.hwrberlin.bidhub.model.server.TransactionDAO;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class OverviewController {

    @FXML
    private TableView<TransactionData> kaufeTable;
    @FXML
    private TableColumn<TransactionData, String> kaufeProduktColumn;
    @FXML
    private TableColumn<TransactionData, Double> kaufePreisColumn;
    @FXML
    private TableColumn<TransactionData, String> kaufeVerkaeuferColumn;

    @FXML
    private TableView<TransactionData> verkaufeTable;
    @FXML
    private TableColumn<TransactionData, String> verkaufeProduktColumn;
    @FXML
    private TableColumn<TransactionData, Double> verkaufePreisColumn;
    @FXML
    private TableColumn<TransactionData, String> verkaufeKaeuferColumn;

    private final OverviewModel handler = new OverviewModel();


    @FXML
    public void initialize() {
        adjustColumnWidths(kaufeTable);
        adjustColumnWidths(verkaufeTable);
        setupTable();
    }

    private void setupTable() {
        kaufeProduktColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().produktName()));
        kaufePreisColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().preis()));
        kaufeVerkaeuferColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().sellerUsername()));

        verkaufeProduktColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().produktName()));
        verkaufePreisColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().preis()));
        verkaufeKaeuferColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().sellerUsername()));

        //loadKaufeData();
        //loadVerkaufeData();
        loadTransactions();
    }

    private void loadTransactions() {
        ArrayList<TransactionData> transactions = handler.getTransactionData();
        ObservableList<TransactionData> observableTransactions = FXCollections.observableArrayList(transactions);
        //kaufeTable.setItems(observableTransactions);
    }


    private void loadKaufeData() {
        ObservableList<TransactionData> data = FXCollections.observableArrayList(
                new TransactionData(1, "Verkäufer1", "Produkt1", 20.0),
                new TransactionData(2, "Verkäufer2", "Produkt2", 35.0)
        );
        kaufeTable.setItems(data);
    }
    private void loadVerkaufeData() {
        ObservableList<TransactionData> data = FXCollections.observableArrayList(
                new TransactionData(1, "Käufer1", "Produkt1", 20.0),
                new TransactionData(2, "Käufer2", "Produkt2", 35.0)
        );
        verkaufeTable.setItems(data);
    }


    private void adjustColumnWidths(TableView<?> tableView) {
        ObservableList<? extends TableColumn<?, ?>> columns = tableView.getColumns();
        columns.forEach(column -> column.prefWidthProperty().bind(tableView.widthProperty().divide(columns.size())));
    }
}
