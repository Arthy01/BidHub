package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.json.dataTypes.AuctionInfo;
import de.hwrberlin.bidhub.json.dataTypes.ProductInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Stellt Methoden zur Interaktion mit der Produktdatenbank bereit.
 * Diese Klasse ermöglicht das Erstellen neuer Produkte und das Verkaufen von Produkten durch Auktionen.
 */
public abstract class ProductDAO {
    /**
     * Erstellt ein neues Produkt in der Datenbank.
     *
     * @param product Informationen zum Produkt, einschließlich Titel, Beschreibung und Verkäufer.
     * @param minimumBid Der Mindestgebotswert für das Produkt.
     * @param minimumIncrement Der Mindestbetrag, um den jedes neue Gebot das vorherige übersteigen muss.
     * @return Die ID des neu erstellten Produkts in der Datenbank oder -1, falls ein Fehler auftritt.
     */
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

    /**
     * Führt den Verkauf eines Produkts durch, indem eine Transaktion in der Datenbank erstellt wird.
     *
     * @param auctionInfo Informationen zur Auktion, einschließlich des Verkaufspreises, des Käufers, des Verkäufers und des Produkts.
     */
    public static void sell(AuctionInfo auctionInfo){
        SQL.update("INSERT INTO Transactions (Sale_Price, Buyer, Seller, Sale_Time, Product_ID) VALUES (?, ?, ?, NOW(), ?)",
            auctionInfo.getBidData().bid(), auctionInfo.getBidData().client().getId(), auctionInfo.getProduct().seller().getId(), auctionInfo.getProduct().id());
    }
}
