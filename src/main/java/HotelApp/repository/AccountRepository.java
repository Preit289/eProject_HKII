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

    // ------------------- CREATE -------------------
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

    // ------------------- UPDATE PASSWORD (chỉ đổi mật khẩu) -------------------
    public static boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE Account_Management SET pass=? WHERE username=?";
        try (Connection conn = DButil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update password failed: " + e.getMessage());
        }
        return false;
    }

    // ------------------- UPDATE ACCOUNT (đổi quyền + mật khẩu nếu có)
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
            System.err.println("Update account failed: " + e.getMessage());
        }
        return false;
    }

    // ------------------- DELETE -------------------
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

    // ------------------- AUTH -------------------
    public static boolean login(String username, String password) {
        String sql = "SELECT username, pass, is_admin FROM Account_Management WHERE username=? AND pass=?";
        try (Connection conn = DButil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean isAdmin = rs.getBoolean("is_admin");
                    String dbPass = rs.getString("pass");

                    currentUser = new Account(
                            rs.getString("username"),
                            dbPass,
                            isAdmin);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return false;
    }

    // ------------------- Logout -------------------
    public static void logout() {
        currentUser = null;
    }

    // ------------------- GET ALL -------------------
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
                        isAdmin));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
        }
        return list;
    }

    // ------------------- RESET TO DEFAULT PASSWORD -------------------
    public static boolean resetAccount(String username) {
        String defaultPassword = "123456"; // pass mặc định
        String sql = "UPDATE Account_Management SET pass=? WHERE username=?";
        try (Connection conn = DButil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, defaultPassword);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Reset password failed: " + e.getMessage());
        }
        return false;
    }

    

}
