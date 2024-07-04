package de.hwrberlin.bidhub.model.server;


import java.sql.*;
import java.util.List;

/**
 * Stellt Methoden zur Verwaltung der Datenbankverbindung und zur Ausführung von SQL-Operationen bereit.
 * Diese Klasse ermöglicht das Verbinden mit der Datenbank, das Ausführen von Abfragen und Updates sowie das Verwalten von Transaktionen.
 */
public abstract class SQL {
    private static final String noConnectionErrorMsg = "Verbindung zum Server fehlgeschlagen.";
    private static final String host = "localhost";
    private static final String port = "3306";
    private static final String database = "BidHub";
    private static final String username = "BidHub_Prog";
    private static final String password = "securePw0815!";

    private static Connection con;

    /**
     * Gibt eine Nachricht zurück, die angezeigt wird, wenn keine Verbindung zum Server hergestellt werden kann.
     *
     * @return Eine vordefinierte Fehlermeldung als String.
     */
    public static String getNoConnectionErrorMsg(){
        return noConnectionErrorMsg;
    }

    /**
     * Überprüft, ob eine Verbindung zur Datenbank besteht und diese gültig ist.
     *
     * @return {@code true}, wenn eine Verbindung besteht und gültig ist, sonst {@code false}.
     */
    public static boolean isConnected(){
        if (con == null)
            return false;

        try{
            return con.isValid(3);
        }
        catch (SQLException e){
            disconnect();
            return false;
        }
    }

    /**
     * Stellt eine Verbindung zur Datenbank her. Verwendet die vordefinierten Verbindungsinformationen.
     *
     * @throws SQLException Wenn ein Fehler beim Herstellen der Verbindung auftritt.
     */
    public static void connect() throws SQLException {
        System.out.println("Connecting to MySQL");

        con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        System.out.println("MySQL connected!");
    }

    /**
     * Schließt die aktuelle Verbindung, falls vorhanden, und stellt eine neue Verbindung zur Datenbank her.
     *
     * @throws SQLException Wenn ein Fehler beim Herstellen der Verbindung auftritt.
     */
    public static void reconnect() throws SQLException {
        System.out.println("Reconnecting to MySql");

        if (con != null)
            disconnect();

        connect();
    }

    /**
     * Schließt die aktuelle Verbindung zur Datenbank, falls eine besteht.
     */
    public static void disconnect(){
        try{
            con.close();
            con = null;
            System.out.println("Disconnected from MySQL!");
        }
        catch (Exception e){
            System.out.println("Already disconnected from Mysql");
        }
    }

    /**
     * Führt eine SQL-Abfrage mit den angegebenen Parametern aus und gibt das Ergebnis zurück.
     *
     * @param query Der SQL-Abfragestring.
     * @param parameters Die Parameter, die in die Abfrage eingefügt werden sollen.
     * @return Ein {@link ResultSet}, das das Ergebnis der Abfrage enthält.
     * @throws SQLException Wenn ein Fehler bei der Ausführung der Abfrage auftritt.
     */
    public static ResultSet query(String query, Object ...parameters) throws SQLException{
        if (con == null)
            throw new SQLException("Not connected to Mysql");

        PreparedStatement statement = con.prepareStatement(query);

        for (int i = 0; i < parameters.length; i++){
            statement.setObject(i + 1, parameters[i]);
        }

        return statement.executeQuery();
    }

    /**
     * Führt ein SQL-Update oder eine Einfügeoperation mit den angegebenen Parametern aus.
     *
     * @param sql Der SQL-Befehl für das Update oder die Einfügeoperation.
     * @param parameters Die Parameter, die in den Befehl eingefügt werden sollen.
     * @return {@code true}, wenn die Operation erfolgreich war, sonst {@code false}.
     */
    public static boolean update(String sql, Object ...parameters){
        try{
            PreparedStatement statement = con.prepareStatement(sql);
            for (int i = 0; i < parameters.length; i++){
                statement.setObject(i + 1, parameters[i]);
            }
            statement.executeUpdate();
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Führt eine Reihe von SQL-Updates oder Einfügeoperationen als Teil einer Transaktion aus.
     * Alle Operationen werden entweder vollständig ausgeführt oder im Fehlerfall zurückgerollt.
     *
     * @param sqls Eine Liste von SQL-Befehlen, die ausgeführt werden sollen.
     * @param parametersList Eine Liste von Parameterlisten, die den SQL-Befehlen entsprechen.
     * @return {@code true}, wenn die Transaktion erfolgreich war, sonst {@code false}.
     */
    public static boolean updateTransaction(List<String> sqls, List<List<Object>> parametersList){
        try{
            con.setAutoCommit(false);

            for (int i = 0; i < sqls.size(); i++) {
                PreparedStatement statement = con.prepareStatement(sqls.get(i));
                List<Object> parameters = parametersList.get(i);
                for (int j = 0; j < parameters.size(); j++){
                    statement.setObject(j + 1, parameters.get(j));
                }
                statement.executeUpdate();
            }

            con.commit();
            return true;
        }
        catch (SQLException | NullPointerException e){
            if (con != null) {
                try {
                    con.rollback();
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if (con != null)
                    con.setAutoCommit(true);
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
