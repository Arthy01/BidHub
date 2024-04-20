package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.shared.UserInformation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInformationDAO {
    public static UserInformation create(long id) {
        String query = "SELECT ui.User_ID, li.Username, ui.Email_Address, ui.First_Name, ui.Last_Name, ui.IBAN, " +
                "a.Country, a.Street, a.House_Number, a.Postal_Code, a.City " +
                "FROM User_Information ui " +
                "JOIN Login_Information li ON ui.User_ID = li.User_ID " +
                "LEFT OUTER JOIN Address a ON ui.User_ID = a.User_ID " +
                "WHERE ui.User_ID = ?";

        try (ResultSet rs = SQL.query(query, id)){
            if (rs.next()) {
                return new UserInformation(
                        rs.getLong("User_ID"),
                        rs.getString("Username"),
                        rs.getString("Email_Address"),
                        rs.getString("First_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("IBAN"),
                        rs.getString("Country"),
                        rs.getString("Street"),
                        rs.getString("House_Number"),
                        rs.getString("Postal_Code"),
                        rs.getString("City")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user information: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean update(UserInformation data) {
        String sql = "UPDATE User_Information ui " +
                "JOIN Address a ON ui.User_ID = a.User_ID " +
                "SET ui.Email_Address = ?, ui.First_Name = ?, ui.Last_Name = ?, ui.IBAN = ?, " +
                "a.Country = ?, a.Street = ?, a.House_Number = ?, a.Postal_Code = ?, a.City = ? " +
                "WHERE ui.User_ID = ?";

        boolean success = SQL.update(sql,
                data.email(),
                data.firstname(),
                data.lastname(),
                data.iban(),
                data.country(),
                data.street(),
                data.streetnumber(),
                data.postcode(),
                data.city(),
                data.id()
        );

        if (success) {
            System.out.println("User information updated successfully.");
        } else {
            System.err.println("Failed to update user information.");
        }

        return success;
    }
}
