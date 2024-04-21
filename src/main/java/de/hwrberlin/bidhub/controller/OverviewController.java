package de.hwrberlin.bidhub.controller;

import de.hwrberlin.bidhub.ClientApplication;
import de.hwrberlin.bidhub.json.dataTypes.TransactionData;
import de.hwrberlin.bidhub.model.client.OverviewModel;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;

public class OverviewController {

    @FXML
    private TableView<TransactionData> purchasesTable;
    @FXML
    private TableColumn<TransactionData, String> purchasesProductColumn;
    @FXML
    private TableColumn<TransactionData, Double> purchasesPriceColumn;
    @FXML
    private TableColumn<TransactionData, String> purchasesSellerColumn;

    @FXML
    private TableView<TransactionData> salesTable;
    @FXML
    private TableColumn<TransactionData, String> salesProductColumn;
    @FXML
    private TableColumn<TransactionData, Double> salesPriceColumn;
    @FXML
    private TableColumn<TransactionData, String> salesBuyerColumn;

    private final OverviewModel handler = new OverviewModel();


    @FXML
    public void initialize() {
        adjustColumnWidths(purchasesTable);
        adjustColumnWidths(salesTable);
        setupTable();
    }

    private void setupTable() {
        purchasesProductColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().productName()));
        purchasesPriceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().price()));
        purchasesSellerColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().sellerUsername()));

        salesProductColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().productName()));
        salesPriceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().price()));
        salesBuyerColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().sellerUsername()));

        loadTransactions();
    }

    private void loadTransactions() {
        ArrayList<TransactionData> transactions = handler.getTransactionData();

        ArrayList<TransactionData> transactions_purchase = new ArrayList<>();
        ArrayList<TransactionData> transactions_sales = new ArrayList<>();


        long cachedId = ClientApplication.getApplicationClient().getId();

        for (TransactionData transactionData : transactions){
            if (transactionData.sellerID() == cachedId){
                transactions_sales.add(transactionData);
            }
            else{
                transactions_purchase.add(transactionData);
            }
        }

        ObservableList<TransactionData> observableTransactions_purchase = FXCollections.observableArrayList(transactions_purchase);
        ObservableList<TransactionData> observableTransactions_sales = FXCollections.observableArrayList(transactions_sales);

        purchasesTable.setItems(observableTransactions_purchase);
        salesTable.setItems(observableTransactions_sales);
    }


    private void adjustColumnWidths(TableView<?> tableView) {
        ObservableList<? extends TableColumn<?, ?>> columns = tableView.getColumns();
        columns.forEach(column -> column.prefWidthProperty().bind(tableView.widthProperty().divide(columns.size())));
    }
}
