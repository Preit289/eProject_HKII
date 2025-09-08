package HotelApp.db;

import java.sql.*;

public class DButil {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=HotelMng;trustServerCertificate=true;integratedSecurity=true;";
    private static final String USER = "sa";
    private static final String PASS = "YourPassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
