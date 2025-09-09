package HotelApp.repository;

import HotelApp.db.DButil;
import HotelApp.model.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private static Account currentUser;

    public static Account getCurrentUser() {
        return currentUser;
    }

    // ------------------- CRUD -------------------

    public static boolean addAccount(Account account) {
        String sql = "INSERT INTO Account_Management (username, pass, is_admin) VALUES (?, ?, ?)";
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setBoolean(3, account.isAdmin());

            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Add failed: username already exists.");
        } catch (SQLException e) {
            System.err.println("Add failed: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateAccount(String username, String newPassword, boolean isAdmin) {
        StringBuilder sql = new StringBuilder("UPDATE Account_Management SET is_admin=?");
        if (newPassword != null && !newPassword.isEmpty()) {
            sql.append(", pass=?");
        }
        sql.append(" WHERE username=?");

        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setBoolean(1, isAdmin);

            if (newPassword != null && !newPassword.isEmpty()) {
                ps.setString(2, newPassword);
                ps.setString(3, username);
            } else {
                ps.setString(2, username);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteAccount(String username) {
        if (username.equalsIgnoreCase("admin")) {
            System.err.println("Cannot delete default admin.");
            return false;
        }

        String sql = "DELETE FROM Account_Management WHERE username=?";
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete failed: " + e.getMessage());
        }
        return false;
    }

    // ------------------- Auth -------------------

    public static boolean login(String username, String password) {
        String sql = "SELECT username, pass, is_admin FROM Account_Management WHERE username=? AND pass=?";
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean isAdmin = rs.getBoolean("is_admin"); // BIT → boolean
                    currentUser = new Account(
                            rs.getString("username"),
                            rs.getString("pass"),
                            isAdmin
                    );
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return false;
    }

    public static void logout() {
        currentUser = null;
    }

    // ------------------- Get All -------------------

    public static List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT username, pass, is_admin FROM Account_Management";
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                list.add(new Account(
                        rs.getString("username"),
                        rs.getString("pass"),
                        isAdmin
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
        }
        return list;
    }
}
