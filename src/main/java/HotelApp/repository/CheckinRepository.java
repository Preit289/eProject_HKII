package HotelApp.repository;

import HotelApp.db.DButil;
import HotelApp.model.Checkin;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CheckinRepository {

    public static ObservableList<Checkin> getAllBookings() {
        ObservableList<Checkin> data = FXCollections.observableArrayList();

        String sql = """
            SELECT 
                bm.Book_by AS GuestName,
                bm.Book_contact AS GuestPhone,
                bm.Planned_checkin_date,
                bm.Planned_checkout_date,
                STRING_AGG(rm.Room_num, ', ') WITHIN GROUP (ORDER BY rm.Room_num) AS Rooms,
                STRING_AGG(rm.Room_category, ', ') WITHIN GROUP (ORDER BY rm.Room_num) AS Categories,
                STRING_AGG(rm.Room_quality, ', ') WITHIN GROUP (ORDER BY rm.Room_num) AS Types
            FROM Booking_Management bm
            JOIN Booking_Room br ON bm.Booking_id = br.Booking_id
            JOIN Room_Management rm ON br.Room_id = rm.Room_id
            GROUP BY bm.Book_by, bm.Book_contact, bm.Planned_checkin_date, bm.Planned_checkout_date
        """;

        try (Connection conn = DButil.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql); 
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                data.add(new Checkin(
                        rs.getString("GuestName"),
                        rs.getString("GuestPhone"),
                        rs.getString("Rooms"),
                        rs.getString("Categories"),
                        rs.getString("Types"),
                        rs.getTimestamp("Planned_checkin_date").toString(),
                        rs.getTimestamp("Planned_checkout_date").toString()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static ObservableList<Checkin> searchBookingsByPhone(String phone) {
        ObservableList<Checkin> data = FXCollections.observableArrayList();
        String sql = """
            SELECT 
                bm.Book_by AS GuestName,
                bm.Book_contact AS GuestPhone,
                bm.Planned_checkin_date,
                bm.Planned_checkout_date,
                STRING_AGG(rm.Room_num, ', ') WITHIN GROUP (ORDER BY rm.Room_num) AS Rooms,
                STRING_AGG(rm.Room_category, ', ') WITHIN GROUP (ORDER BY rm.Room_num) AS Categories,
                STRING_AGG(rm.Room_quality, ', ') WITHIN GROUP (ORDER BY rm.Room_num) AS Types
            FROM Booking_Management bm
            JOIN Booking_Room br ON bm.Booking_id = br.Booking_id
            JOIN Room_Management rm ON br.Room_id = rm.Room_id
            WHERE bm.Book_contact LIKE ?
            GROUP BY bm.Book_by, bm.Book_contact, bm.Planned_checkin_date, bm.Planned_checkout_date
        """;

        try (Connection conn = DButil.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + phone + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                data.add(new Checkin(
                        rs.getString("GuestName"),
                        rs.getString("GuestPhone"),
                        rs.getString("Rooms"),
                        rs.getString("Categories"),
                        rs.getString("Types"),
                        rs.getTimestamp("Planned_checkin_date").toString(),
                        rs.getTimestamp("Planned_checkout_date").toString()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static String createStayingFromBooking(Checkin booking) throws SQLException {
        String stayingId = null;
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

            // Get Booking_id based on guest phone and other details
            String findBookingIdSql = """
                SELECT bm.Booking_id, bm.Payment_method, bm.Deposit_amount
                FROM Booking_Management bm
                JOIN Booking_Room br ON bm.Booking_id = br.Booking_id
                JOIN Room_Management rm ON br.Room_id = rm.Room_id
                WHERE bm.Book_contact = ? AND bm.Book_by = ?
                GROUP BY bm.Booking_id, bm.Payment_method, bm.Deposit_amount
            """;
            PreparedStatement findStmt = conn.prepareStatement(findBookingIdSql);
            findStmt.setString(1, booking.getGuestPhone());
            findStmt.setString(2, booking.getGuestName());
            ResultSet rs = findStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Booking not found.");
            }

            String bookingId = rs.getString("Booking_id");
            String paymentMethod = rs.getString("Payment_method");
            int depositAmount = rs.getInt("Deposit_amount");

            // Check if a Staying_Management record already exists for this Booking_id
            String checkStayingSql = """
                SELECT Staying_id
                FROM Staying_Management
                WHERE Booking_id = ?
            """;
            PreparedStatement checkStayingStmt = conn.prepareStatement(checkStayingSql);
            checkStayingStmt.setString(1, bookingId);
            ResultSet checkRs = checkStayingStmt.executeQuery();
            if (checkRs.next()) {
                stayingId = checkRs.getString("Staying_id");
                conn.commit();
                throw new SQLException("A stay has already been created for this booking (Staying_id: " + stayingId + ").");
            }

            // Calculate total amount based on room prices
            String totalAmountSql = """
                SELECT SUM(rm.Room_price) AS Total
                FROM Booking_Room br
                JOIN Room_Management rm ON br.Room_id = rm.Room_id
                WHERE br.Booking_id = ?
            """;
            PreparedStatement totalStmt = conn.prepareStatement(totalAmountSql);
            totalStmt.setString(1, bookingId);
            ResultSet totalRs = totalStmt.executeQuery();
            int totalAmount = totalRs.next() ? totalRs.getInt("Total") : 0;

            // Generate new Staying_id
            String stayingIdSql = "SELECT dbo.fn_GenerateNextStayingID() AS Staying_id";
            PreparedStatement stayingIdStmt = conn.prepareStatement(stayingIdSql);
            ResultSet stayingIdRs = stayingIdStmt.executeQuery();
            if (stayingIdRs.next()) {
                stayingId = stayingIdRs.getString("Staying_id");
            }

            // Insert into Staying_Management
            String insertStayingSql = """
                INSERT INTO Staying_Management (
                    Staying_id, Booking_id, Checkin_date, Checkout_date, 
                    Payment_method, Staying_status, Total_amount, 
                    Created_at, Updated_at, By_role
                )
                VALUES (?, ?, ?, ?, ?, 0, ?, GETDATE(), GETDATE(), 'admin')
            """;
            PreparedStatement insertStayingStmt = conn.prepareStatement(insertStayingSql);
            insertStayingStmt.setString(1, stayingId);
            insertStayingStmt.setString(2, bookingId);
            insertStayingStmt.setTimestamp(3, Timestamp.valueOf(booking.getPlannedCheckIn()));
            insertStayingStmt.setTimestamp(4, Timestamp.valueOf(booking.getPlannedCheckOut()));
            insertStayingStmt.setString(5, paymentMethod);
            insertStayingStmt.setInt(6, totalAmount);
            insertStayingStmt.executeUpdate();

            // Update Booking_status to 4 (Room received)
            String updateBookingSql = """
                UPDATE Booking_Management
                SET Booking_status = 4
                WHERE Booking_id = ?
            """;
            PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingSql);
            updateBookingStmt.setString(1, bookingId);
            updateBookingStmt.executeUpdate();

            // Copy rooms from Booking_Room to Staying_Room_Customer
            String insertRoomsSql = """
                INSERT INTO Staying_Room_Customer (Staying_id, Room_id, Customer_id)
                SELECT ?, br.Room_id, cm.Customer_id
                FROM Booking_Room br
                JOIN Customer_Management cm ON cm.Phone_num = ?
                WHERE br.Booking_id = ?
            """;
            PreparedStatement insertRoomsStmt = conn.prepareStatement(insertRoomsSql);
            insertRoomsStmt.setString(1, stayingId);
            insertRoomsStmt.setString(2, booking.getGuestPhone());
            insertRoomsStmt.setString(3, bookingId);
            insertRoomsStmt.executeUpdate();

            // Commit transaction
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return stayingId;
    }

    public static String assignCustomerToRoom(String stayingId, String roomNumber, String phone) throws SQLException {
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

            // Find Room_id by Room_num
            String findRoomIdSql = """
                SELECT Room_id
                FROM Room_Management
                WHERE Room_num = ?
            """;
            PreparedStatement findRoomStmt = conn.prepareStatement(findRoomIdSql);
            findRoomStmt.setString(1, roomNumber);
            ResultSet roomRs = findRoomStmt.executeQuery();
            if (!roomRs.next()) {
                throw new SQLException("Room not found: " + roomNumber);
            }
            String roomId = roomRs.getString("Room_id");

            // Check if customer exists by phone number
            String findCustomerSql = """
                SELECT Customer_id, Customer_name
                FROM Customer_Management
                WHERE Phone_num = ?
            """;
            PreparedStatement findCustomerStmt = conn.prepareStatement(findCustomerSql);
            findCustomerStmt.setString(1, phone);
            ResultSet customerRs = findCustomerStmt.executeQuery();
            String customerId;

            if (customerRs.next()) {
                customerId = customerRs.getString("Customer_id");
            } else {
                // Create a new customer
                customerId = generateNewCustomerId(conn);
                String insertCustomerSql = """
                    INSERT INTO Customer_Management (
                        Customer_id, Customer_name, Phone_num, Is_foreigner, Gender, Is_child
                    )
                    VALUES (?, ?, ?, 0, 1, 0)
                """;
                PreparedStatement insertCustomerStmt = conn.prepareStatement(insertCustomerSql);
                insertCustomerStmt.setString(1, customerId);
                insertCustomerStmt.setString(2, "Customer_" + phone); // Placeholder name
                insertCustomerStmt.setString(3, phone);
                insertCustomerStmt.executeUpdate();
            }

            // Check if the customer is already assigned to the room
            String checkAssignmentSql = """
                SELECT 1
                FROM Staying_Room_Customer
                WHERE Staying_id = ? AND Room_id = ? AND Customer_id = ?
            """;
            PreparedStatement checkAssignmentStmt = conn.prepareStatement(checkAssignmentSql);
            checkAssignmentStmt.setString(1, stayingId);
            checkAssignmentStmt.setString(2, roomId);
            checkAssignmentStmt.setString(3, customerId);
            ResultSet checkRs = checkAssignmentStmt.executeQuery();
            if (checkRs.next()) {
                conn.commit();
                return customerId; // Already assigned
            }

            // Assign customer to room
            String assignSql = """
                INSERT INTO Staying_Room_Customer (Staying_id, Room_id, Customer_id)
                VALUES (?, ?, ?)
            """;
            PreparedStatement assignStmt = conn.prepareStatement(assignSql);
            assignStmt.setString(1, stayingId);
            assignStmt.setString(2, roomId);
            assignStmt.setString(3, customerId);
            assignStmt.executeUpdate();

            // Commit transaction
            conn.commit();
            return customerId;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static String generateNewCustomerId(Connection conn) throws SQLException {
        String sql = "SELECT dbo.fn_GenerateNextCustomerID() AS Customer_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("Customer_id");
            }
            throw new SQLException("Failed to generate new Customer_id");
        }
    }
}