package de.hwrberlin.bidhub.model.server;

import de.hwrberlin.bidhub.model.shared.UserInformation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Stellt Datenzugriffsmethoden für Benutzerinformationen bereit.
 * Ermöglicht das Abrufen und Aktualisieren von Benutzerinformationen in der Datenbank.
 */
public class UserInformationDAO {
    /**
     * Erstellt ein {@link UserInformation}-Objekt für einen gegebenen Benutzer-ID.
     * Führt eine Datenbankabfrage durch, um Benutzerinformationen zu erhalten und gibt ein entsprechendes {@link UserInformation}-Objekt zurück.
     *
     * @param id Die ID des Benutzers, für den die Informationen abgerufen werden sollen.
     * @return Ein {@link UserInformation}-Objekt mit den abgerufenen Daten oder null, wenn kein Benutzer mit der angegebenen ID gefunden wurde.
     */
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

    /**
     * Aktualisiert die Informationen eines Benutzers in der Datenbank.
     * Führt Transaktionen aus, um die Benutzerinformationen und die zugehörige Adresse zu aktualisieren.
     *
     * @param data Ein {@link UserInformation}-Objekt, das die zu aktualisierenden Daten enthält.
     * @return true, wenn die Aktualisierung erfolgreich war, sonst false.
     */
    public static boolean update(UserInformation data) {
        String sql1 = "UPDATE User_Information " +
                "SET Email_Address = ?, First_Name = ?, Last_Name = ?, IBAN = ? " +
                "WHERE User_ID = ?";

        String sql2 = "UPDATE Address " +
                "SET Country = ?, Street = ?, House_Number = ?, Postal_Code = ?, City = ? " +
                "WHERE User_ID = ?";

        boolean success = SQL.updateTransaction(
                Arrays.asList(sql1, sql2),
                Arrays.asList(
                    Arrays.asList(
                            data.email(),
                            data.firstname(),
                            data.lastname(),
                            data.iban(),
                            data.id()
                    ),
                    Arrays.asList(
                            data.country(),
                            data.street(),
                            data.streetnumber(),
                            data.postcode(),
                            data.city(),
                            data.id()
                    )
                )
        );

        if (success) {
            System.out.println("User information updated successfully.");
        } else {
            System.err.println("Failed to update user information.");
        }

        return success;
    }
}
