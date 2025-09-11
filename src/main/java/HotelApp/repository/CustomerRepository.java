package HotelApp.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import HotelApp.db.DButil;
import HotelApp.model.Customer;

public class CustomerRepository {

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customer_Management";

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("Customer_id"),
                        rs.getString("Customer_name"),
                        rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null,
                        rs.getString("Citizen_identity_number"),
                        rs.getBoolean("Is_foreigner"),
                        rs.getBoolean("Gender"),
                        rs.getBoolean("Is_child"),
                        rs.getString("Phone_num")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public boolean addCustomer(Customer customer) {
        String query = """
                      INSERT INTO Customer_Management 
                      (Customer_id, Customer_name, DOB, Citizen_identity_number, 
                      Is_foreigner, Gender, Is_child, Phone_num)
                      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                      """;

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getCustomerName());
            if (customer.getDob() != null) {
                pstmt.setDate(3, Date.valueOf(customer.getDob()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            pstmt.setString(4, customer.getCitizenIdentityNumber());
            pstmt.setBoolean(5, customer.isIsForeigner());
            pstmt.setBoolean(6, customer.isGender());
            pstmt.setBoolean(7, customer.isIsChild());
            pstmt.setString(8, customer.getPhoneNum());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomer(Customer customer) {
        String query = """
                      UPDATE Customer_Management 
                      SET Customer_name = ?, DOB = ?, Citizen_identity_number = ?, 
                          Is_foreigner = ?, Gender = ?, Is_child = ?, Phone_num = ?
                      WHERE Customer_id = ?
                      """;

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getCustomerName());
            if (customer.getDob() != null) {
                pstmt.setDate(2, Date.valueOf(customer.getDob()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setString(3, customer.getCitizenIdentityNumber());
            pstmt.setBoolean(4, customer.isIsForeigner());
            pstmt.setBoolean(5, customer.isGender());
            pstmt.setBoolean(6, customer.isIsChild());
            pstmt.setString(7, customer.getPhoneNum());
            pstmt.setString(8, customer.getCustomerId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(String customerId) {
        String query = "DELETE FROM Customer_Management WHERE Customer_id = ?";

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getNextCustomerId() {
        String query = "SELECT dbo.fn_GenerateNextCustomerID()";
        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer findCustomerByIdentityNumber(String identityNumber) {
        String query = "SELECT * FROM Customer_Management WHERE Citizen_identity_number = ?";

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, identityNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getString("Customer_id"),
                            rs.getString("Customer_name"),
                            rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null,
                            rs.getString("Citizen_identity_number"),
                            rs.getBoolean("Is_foreigner"),
                            rs.getBoolean("Gender"),
                            rs.getBoolean("Is_child"),
                            rs.getString("Phone_num")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
