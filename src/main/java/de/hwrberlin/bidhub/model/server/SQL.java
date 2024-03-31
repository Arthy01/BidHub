package de.hwrberlin.bidhub.model.server;


import java.sql.*;
import java.util.List;

public abstract class SQL {
    private static final String noConnectionErrorMsg = "Verbindung zum Server fehlgeschlagen.";
    private static final String host = "localhost";
    private static final String port = "3306";
    private static final String database = "BidHub";
    private static final String username = "BidHub_Prog";
    private static final String password = "securePw0815!";

    private static Connection con;

    public static String getNoConnectionErrorMsg(){
        return noConnectionErrorMsg;
    }

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

    public static void connect() throws SQLException {
        System.out.println("Connecting to MySQL");

        con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        System.out.println("MySQL connected!");
    }

    public static void reconnect() throws SQLException {
        System.out.println("Reconnecting to MySql");

        if (con != null)
            disconnect();

        connect();
    }

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

    public static ResultSet query(String query, Object ...parameters) throws SQLException{
        if (con == null)
            throw new SQLException("Not connected to Mysql");

        PreparedStatement statement = con.prepareStatement(query);

        for (int i = 0; i < parameters.length; i++){
            statement.setObject(i + 1, parameters[i]);
        }

        return statement.executeQuery();
    }

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
