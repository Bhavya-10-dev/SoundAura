package soundaura.dbms;

import java.sql.*;

public class JdbcConnection{
    private static String url = "jdbc:mysql://localhost:3306/soundaura";
    private static String user = "root";
    private static String password = "";
    // public static Connection con;
    
    // static{
    //     try {
    //         con = DriverManager.getConnection(url, user, password);
    //         System.out.println("\nConnection established successfully!\n");
    //     } 
    //     catch (SQLException e) {
    //         System.out.println("\nFailed to establish connection.\n");
    //         e.printStackTrace();
    //     }
    // }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } 
        catch (SQLException e) {
            return null;
        }
    }
}
