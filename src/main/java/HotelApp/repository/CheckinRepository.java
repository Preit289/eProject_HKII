package HotelApp.repository;

import HotelApp.db.DButil;
import HotelApp.model.Checkin;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;

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

            String stayingIdSql = "SELECT dbo.fn_GenerateNextStayingID() AS Staying_id";
            PreparedStatement stayingIdStmt = conn.prepareStatement(stayingIdSql);
            ResultSet stayingIdRs = stayingIdStmt.executeQuery();
            if (stayingIdRs.next()) {
                stayingId = stayingIdRs.getString("Staying_id");
            }

            String insertStayingSql = """
                INSERT INTO Staying_Management (
                    Staying_id, Booking_id, Checkin_date, Checkout_date, 
                    Payment_method, Staying_status, Total_amount, 
                    Created_at, Updated_at, By_role
                )
                VALUES (?, ?, NULL, ?, ?, 0, ?, GETDATE(), GETDATE(), 'admin')
            """;
            PreparedStatement insertStayingStmt = conn.prepareStatement(insertStayingSql);
            insertStayingStmt.setString(1, stayingId);
            insertStayingStmt.setString(2, bookingId);
            insertStayingStmt.setTimestamp(3, Timestamp.valueOf(booking.getPlannedCheckOut()));
            insertStayingStmt.setString(4, paymentMethod);
            insertStayingStmt.setInt(5, totalAmount);
            insertStayingStmt.executeUpdate();

            String updateBookingSql = """
                UPDATE Booking_Management
                SET Booking_status = 4
                WHERE Booking_id = ?
            """;
            PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingSql);
            updateBookingStmt.setString(1, bookingId);
            updateBookingStmt.executeUpdate();

            String insertRoomsSql = """
                INSERT INTO Staying_Room_Customer (Staying_id, Room_id, Customer_id)
                SELECT ?, br.Room_id, cm.Customer_id
                FROM Booking_Room br
                JOIN Customer_Management cm ON cm.Phone_num = ?
                WHERE br.Booking_id = ?
                AND NOT EXISTS (
                    SELECT 1
                    FROM Staying_Room_Customer src
                    WHERE src.Staying_id = ? AND src.Room_id = br.Room_id AND src.Customer_id = cm.Customer_id
                )
            """;
            PreparedStatement insertRoomsStmt = conn.prepareStatement(insertRoomsSql);
            insertRoomsStmt.setString(1, stayingId);
            insertRoomsStmt.setString(2, booking.getGuestPhone());
            insertRoomsStmt.setString(3, bookingId);
            insertRoomsStmt.setString(4, stayingId);
            insertRoomsStmt.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return stayingId;
    }

    public static Map<String, String> getAllCustomers() {
        Map<String, String> customerMap = new HashMap<>();
        String sql = """
            SELECT Customer_id, Customer_name
            FROM Customer_Management
            ORDER BY Customer_name
        """;

        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customerMap.put(rs.getString("Customer_name"), rs.getString("Customer_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerMap;
    }

    public static Map<String, String> getCustomersForRoom(String stayingId, String roomNumber) {
        Map<String, String> customerMap = new HashMap<>();
        String sql = """
            SELECT cm.Customer_id, cm.Customer_name
            FROM Staying_Room_Customer src
            JOIN Room_Management rm ON src.Room_id = rm.Room_id
            JOIN Customer_Management cm ON src.Customer_id = cm.Customer_id
            WHERE src.Staying_id = ? AND rm.Room_num = ?
            ORDER BY cm.Customer_name
        """;

        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stayingId);
            pstmt.setString(2, roomNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customerMap.put(rs.getString("Customer_name"), rs.getString("Customer_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerMap;
    }

    public static void assignCustomerToRoom(String stayingId, String roomNumber, String customerId) throws SQLException {
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

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
                return;
            }

            String assignSql = """
                INSERT INTO Staying_Room_Customer (Staying_id, Room_id, Customer_id)
                VALUES (?, ?, ?)
            """;
            PreparedStatement assignStmt = conn.prepareStatement(assignSql);
            assignStmt.setString(1, stayingId);
            assignStmt.setString(2, roomId);
            assignStmt.setString(3, customerId);
            assignStmt.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void removeCustomerFromRoom(String stayingId, String roomNumber, String customerId) throws SQLException {
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

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

            String deleteSql = """
                DELETE FROM Staying_Room_Customer
                WHERE Staying_id = ? AND Room_id = ? AND Customer_id = ?
            """;
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setString(1, stayingId);
            deleteStmt.setString(2, roomId);
            deleteStmt.setString(3, customerId);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No customer assignment found to remove.");
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Map<String, String> getAllServices() {
        Map<String, String> serviceMap = new HashMap<>();
        String sql = """
            SELECT Service_id, Service_name
            FROM Service_Management
            ORDER BY Service_name
        """;

        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                serviceMap.put(rs.getString("Service_name"), rs.getString("Service_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return serviceMap;
    }

    public static Map<String, String> getServicesForRoom(String stayingId, String roomNumber) {
        Map<String, String> serviceMap = new HashMap<>();
        String sql = """
            SELECT sm.Service_id, sm.Service_name
            FROM Staying_Room_Service srs
            JOIN Room_Management rm ON srs.Room_id = rm.Room_id
            JOIN Service_Management sm ON srs.Service_id = sm.Service_id
            WHERE srs.Staying_id = ? AND rm.Room_num = ?
            ORDER BY sm.Service_name
        """;

        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stayingId);
            pstmt.setString(2, roomNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                serviceMap.put(rs.getString("Service_name"), rs.getString("Service_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return serviceMap;
    }

    public static void assignServiceToRoom(String stayingId, String roomNumber, String serviceId) throws SQLException {
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

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

            String checkAssignmentSql = """
                SELECT Quantity
                FROM Staying_Room_Service
                WHERE Staying_id = ? AND Room_id = ? AND Service_id = ?
            """;
            PreparedStatement checkAssignmentStmt = conn.prepareStatement(checkAssignmentSql);
            checkAssignmentStmt.setString(1, stayingId);
            checkAssignmentStmt.setString(2, roomId);
            checkAssignmentStmt.setString(3, serviceId);
            ResultSet checkRs = checkAssignmentStmt.executeQuery();

            if (checkRs.next()) {
                int currentQuantity = checkRs.getInt("Quantity");
                String updateSql = """
                    UPDATE Staying_Room_Service
                    SET Quantity = ?
                    WHERE Staying_id = ? AND Room_id = ? AND Service_id = ?
                """;
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, currentQuantity + 1);
                updateStmt.setString(2, stayingId);
                updateStmt.setString(3, roomId);
                updateStmt.setString(4, serviceId);
                updateStmt.executeUpdate();
            } else {
                String assignSql = """
                    INSERT INTO Staying_Room_Service (Staying_id, Room_id, Service_id, Quantity)
                    VALUES (?, ?, ?, 1)
                """;
                PreparedStatement assignStmt = conn.prepareStatement(assignSql);
                assignStmt.setString(1, stayingId);
                assignStmt.setString(2, roomId);
                assignStmt.setString(3, serviceId);
                assignStmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void removeServiceFromRoom(String stayingId, String roomNumber, String serviceId) throws SQLException {
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

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

            String deleteSql = """
                DELETE FROM Staying_Room_Service
                WHERE Staying_id = ? AND Room_id = ? AND Service_id = ?
            """;
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setString(1, stayingId);
            deleteStmt.setString(2, roomId);
            deleteStmt.setString(3, serviceId);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No service assignment found to remove.");
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void updateServiceQuantity(String stayingId, String roomNumber, String serviceId, int quantity) throws SQLException {
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

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

            String updateSql = """
                UPDATE Staying_Room_Service
                SET Quantity = ?
                WHERE Staying_id = ? AND Room_id = ? AND Service_id = ?
            """;
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, quantity);
            updateStmt.setString(2, stayingId);
            updateStmt.setString(3, roomId);
            updateStmt.setString(4, serviceId);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No service assignment found to update quantity.");
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String getCheckinDate(String stayingId) {
        String sql = """
            SELECT Checkin_date
            FROM Staying_Management
            WHERE Staying_id = ?
        """;
        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stayingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getTimestamp("Checkin_date") != null) {
                return rs.getTimestamp("Checkin_date").toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void updateBookingAndStay(String guestPhone, String guestName, String stayingId, 
                                          String paymentMethod, int depositAmount) throws SQLException {
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

            String findBookingIdSql = """
                SELECT Booking_id
                FROM Booking_Management
                WHERE Book_contact = ? AND Book_by = ?
            """;
            PreparedStatement findStmt = conn.prepareStatement(findBookingIdSql);
            findStmt.setString(1, guestPhone);
            findStmt.setString(2, guestName);
            ResultSet rs = findStmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Booking not found for phone: " + guestPhone + " and name: " + guestName);
            }
            String bookingId = rs.getString("Booking_id");

            String totalAmountSql = """
                SELECT 
                    COALESCE(SUM(rm.Room_price), 0) + COALESCE(SUM(sm.Service_price * srs.Quantity), 0) AS Total
                FROM Booking_Room br
                JOIN Room_Management rm ON br.Room_id = rm.Room_id
                LEFT JOIN Staying_Room_Service srs ON br.Room_id = srs.Room_id AND srs.Staying_id = ?
                LEFT JOIN Service_Management sm ON srs.Service_id = sm.Service_id
                WHERE br.Booking_id = ?
                GROUP BY br.Booking_id
            """;
            PreparedStatement totalStmt = conn.prepareStatement(totalAmountSql);
            totalStmt.setString(1, stayingId);
            totalStmt.setString(2, bookingId);
            ResultSet totalRs = totalStmt.executeQuery();
            int totalAmount = totalRs.next() ? totalRs.getInt("Total") : 0;

            String updateBookingSql = """
                UPDATE Booking_Management
                SET Payment_method = ?, Deposit_amount = ?, Updated_at = GETDATE()
                WHERE Booking_id = ?
            """;
            PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingSql);
            updateBookingStmt.setString(1, paymentMethod);
            updateBookingStmt.setInt(2, depositAmount);
            updateBookingStmt.setString(3, bookingId);
            updateBookingStmt.executeUpdate();

            String updateStayingSql = """
                UPDATE Staying_Management
                SET Payment_method = ?, Total_amount = ?, Updated_at = GETDATE()
                WHERE Staying_id = ?
            """;
            PreparedStatement updateStayingStmt = conn.prepareStatement(updateStayingSql);
            updateStayingStmt.setString(1, paymentMethod);
            updateStayingStmt.setInt(2, totalAmount);
            updateStayingStmt.setString(3, stayingId);
            updateStayingStmt.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void performCheckin(String stayingId) throws SQLException {
        try (Connection conn = DButil.getConnection()) {
            conn.setAutoCommit(false);

            String checkStayingSql = """
                SELECT Staying_id
                FROM Staying_Management
                WHERE Staying_id = ?
            """;
            PreparedStatement checkStmt = conn.prepareStatement(checkStayingSql);
            checkStmt.setString(1, stayingId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Stay not found: " + stayingId);
            }

            String updateStayingSql = """
                UPDATE Staying_Management
                SET Checkin_date = GETDATE(), Staying_status = 1, Updated_at = GETDATE()
                WHERE Staying_id = ?
            """;
            PreparedStatement updateStmt = conn.prepareStatement(updateStayingSql);
            updateStmt.setString(1, stayingId);
            updateStmt.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}