package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.json.dataTypes.AuctionInfo;
import de.hwrberlin.bidhub.json.dataTypes.ProductInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ProductDAO {
    public static long create(ProductInfo product, float minimumBid, float minimumIncrement){
        if (SQL.update("INSERT INTO Product (Product_Name, Product_Description, Minimum_increment, Minimum_Bid, Creator_User_ID) VALUES (?, ?, ?, ?, ?)",
                product.title(), product.description(), minimumIncrement, minimumBid, product.seller().getId())){

            try (ResultSet resultSet = SQL.query("SELECT LAST_INSERT_ID() AS ID")){
                if (resultSet.next()){
                    return resultSet.getLong("ID");
                }
            }
            catch (SQLException e){
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public static void sell(AuctionInfo auctionInfo){
        SQL.update("INSERT INTO Transactions (Sale_Price, Buyer, Seller, Sale_Time, Product_ID) VALUES (?, ?, ?, NOW(), ?)",
            auctionInfo.getBidData().bid(), auctionInfo.getBidData().client().getId(), auctionInfo.getProduct().seller().getId(), auctionInfo.getProduct().id());
    }
}
