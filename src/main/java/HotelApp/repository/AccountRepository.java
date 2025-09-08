package HotelApp.repository;

import java.sql.*;
import HotelApp.db.DButil;
import HotelApp.model.Account;

public class AccountRepository {

    private static Account currentUser;

    public static Account getCurrentUser() {
        return currentUser;
    }

    // Login
    public static boolean login(String username, String password) {
        String sql = "SELECT username, pass, is_admin FROM Account_Management WHERE username=? AND pass=?";
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentUser = new Account(
                            rs.getString("username"),
                            rs.getString("pass"),
                            rs.getBoolean("is_admin")
                    );
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return false;
    }

    // Register
    public static boolean register(String username, String password, boolean isAdmin) {
        String sql = "INSERT INTO Account_Management (username, pass, is_admin) VALUES (?, ?, ?)";
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setBoolean(3, isAdmin);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Register failed: " + e.getMessage());
            return false;
        }
    }

    public static void logout() {
        currentUser = null;
    }
}
