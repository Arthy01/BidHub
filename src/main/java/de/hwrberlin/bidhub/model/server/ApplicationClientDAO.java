package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.server.SQL;
import de.hwrberlin.bidhub.model.shared.ApplicationClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class ApplicationClientDAO {
    public static ApplicationClient authenticate(String username, String hashedPassword){
        ApplicationClient result = null;
        try(ResultSet resultSet = SQL.query("SELECT l.User_ID, i.Email_Address, i.First_Name, i.Last_Name, i.IBAN FROM Login_Information l JOIN User_Information i WHERE BINARY l.Username = ? AND BINARY l.Password = ?", username, hashedPassword)){
            if(resultSet.next()){
                result = new ApplicationClient(resultSet.getLong("User_ID"), username,  resultSet.getString("Email_Address"), resultSet.getString("First_Name"), resultSet.getString("Last_Name"), resultSet.getString("IBAN"));
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static boolean isAvailable(String username){
        try(ResultSet resultSet = SQL.query("SELECT User_ID FROM Login_Information WHERE Username = ?", username)){
            return !resultSet.next();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean create(String username, String hashedPassword, String email) {
        return SQL.updateTransaction(
                Arrays.asList(
                        "INSERT INTO Login_Information(Username, Password) VALUES(?, ?)",
                        "INSERT INTO User_Information (User_ID, Email_Address) VALUES (LAST_INSERT_ID(), ?);"
                ),
                Arrays.asList(
                        Arrays.asList(username, hashedPassword),
                        Arrays.asList(email)
                )
        );
    }

    public static boolean updateFirstName(long userId, String firstName){
        return SQL.update("UPDATE User_Information SET First_Name = ? WHERE User_ID = ?", firstName, userId);
    }

    public static boolean updateLastName(long userId, String lastName){
        return SQL.update("UPDATE User_Information SET Last_Name = ? WHERE User_ID = ?", lastName, userId);
    }

    public static boolean updateIban(long userId, String iban){
        return SQL.update("UPDATE User_Information SET IBAN = ? WHERE User_ID = ?", iban, userId);
    }
}
