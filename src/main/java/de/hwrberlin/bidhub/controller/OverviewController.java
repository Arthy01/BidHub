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

/**
 * Der OverviewController ist für die Handhabung der Benutzeroberfläche zur Anzeige
 * von Kauf- und Verkaufsübersichten zuständig.
 */
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

    /**
     * Initialisiert die Controller-Klasse. Diese Methode wird automatisch aufgerufen,
     * nachdem die FXML-Datei geladen wurde.
     */
    @FXML
    public void initialize() {
        adjustColumnWidths(purchasesTable);
        adjustColumnWidths(salesTable);
        setupTable();
    }

    /**
     * Richtet die Tabellen zur Anzeige der Transaktionsdaten ein.
     */
    private void setupTable() {
        purchasesProductColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().productName()));
        purchasesPriceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().price()));
        purchasesSellerColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().sellerUsername()));

        salesProductColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().productName()));
        salesPriceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().price()));
        salesBuyerColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().buyerUsername()));

        loadTransactions();
    }

    /**
     * Lädt die Transaktionsdaten und ordnet sie den entsprechenden Tabellen zu.
     */
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

    /**
     * Passt die Spaltenbreiten der angegebenen Tabelle gleichmäßig an.
     *
     * @param tableView die Tabelle, deren Spaltenbreiten angepasst werden sollen
     */
    private void adjustColumnWidths(TableView<?> tableView) {
        ObservableList<? extends TableColumn<?, ?>> columns = tableView.getColumns();
        columns.forEach(column -> column.prefWidthProperty().bind(tableView.widthProperty().divide(columns.size())));
    }
}
