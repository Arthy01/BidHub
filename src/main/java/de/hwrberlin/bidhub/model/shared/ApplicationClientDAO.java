package de.hwrberlin.bidhub.model.shared;

import de.hwrberlin.bidhub.model.server.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ApplicationClientDAO {
    public static ApplicationClient authenticate(String username, String hashedPassword){
        ApplicationClient result = null;
        try(ResultSet resultSet = SQL.query("SELECT User_ID FROM Login_Information WHERE Username = ? AND Password = ?", username, hashedPassword)){
            if(resultSet.next()){
                result = new ApplicationClient(username, resultSet.getLong("User_ID"));
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static void create(ApplicationClient client) {

    }

    public static void update(ApplicationClient client){

    }
}
