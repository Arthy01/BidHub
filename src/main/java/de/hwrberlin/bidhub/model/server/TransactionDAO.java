package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.json.dataTypes.TransactionData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.DriverManager.getConnection;

public class TransactionDAO {
    public static ArrayList<TransactionData> getAllTransactionInformation(long userId) {
        String query = "SELECT tr.Seller, tr.Sale_Price, li.Username As Seller_Username, pr.Product_Name , li_.Username As Buyer_Username " +
                "FROM Transactions tr " +
                "JOIN Login_Information li ON li.User_ID = tr.Seller " +
                "JOIN Login_Information li_ ON li_.User_ID = tr.Buyer " +
                "JOIN Product pr ON pr.Product_ID = tr.Product_ID " +
                "WHERE tr.Seller = ? OR tr.Buyer = ?";

        ArrayList<TransactionData> transactions = new ArrayList<>();

        try (ResultSet rs = SQL.query(query, userId, userId)) {
            while (rs.next()) {
                transactions.add(new TransactionData(
                        rs.getLong("Seller"),
                        rs.getString("Seller_Username"),
                        rs.getString("Buyer_Username"),
                        rs.getString("Product_Name"),
                        rs.getDouble("Sale_Price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }
}