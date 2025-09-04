package HotelApp.repository;

import HotelApp.db.DButil;
import HotelApp.model.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public final class BookingRepository {
    private BookingRepository(){}

    /** Helper convert */
    private static LocalDate toLocalDate(Timestamp ts){
        return ts == null ? null : ts.toLocalDateTime().toLocalDate();
    }

    /** Lấy toàn bộ booking kèm Room_num (nếu có) để dùng cho bảng và checkout */
    public static List<Booking> getAll() {
        final String sql =
            "SELECT b.Booking_id, b.Booking_status, b.Book_contact, " +
            "       b.Planned_checkin_date, b.Planned_checkout_date, " +
            "       rm.Room_num, rm.Room_price " +
            "FROM dbo.Booking_Management b " +
            "LEFT JOIN dbo.Booking_Room br ON br.Booking_id = b.Booking_id " +
            "LEFT JOIN dbo.Room_Management rm ON rm.Room_id = br.Room_id " +
            "ORDER BY b.Booking_date DESC";
        List<Booking> list = new ArrayList<>();
        try (Connection c = DButil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("Booking_id");
                int status = rs.getInt("Booking_status");
                String contact = rs.getString("Book_contact"); // người đặt (text)
                LocalDate ci = toLocalDate(rs.getTimestamp("Planned_checkin_date"));
                LocalDate co = toLocalDate(rs.getTimestamp("Planned_checkout_date"));
                String roomNum = rs.getString("Room_num");
                double price = rs.getDouble("Room_price");

                Booking b = new Booking(id, contact, roomNum, ci, co, status);
                b.setPrice(price);
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** SELECT kiểm tra conflict. Dùng cùng connection/transaction để an toàn. */
    private static boolean hasConflictTx(Connection c, String roomNum, LocalDate ci, LocalDate co) throws SQLException {
        final String sql =
            "SELECT 1 " +
            "FROM dbo.Booking_Management bm WITH (UPDLOCK, HOLDLOCK) " +
            "JOIN dbo.Booking_Room br WITH (UPDLOCK, HOLDLOCK) ON br.Booking_id = bm.Booking_id " +
            "JOIN dbo.Room_Management rm ON rm.Room_id = br.Room_id " +
            "WHERE LTRIM(RTRIM(rm.Room_num)) = LTRIM(RTRIM(?)) " +
            "  AND bm.Booking_status IN (2,3,4) " +
            "  AND NOT (bm.Planned_checkout_date <= ? OR bm.Planned_checkin_date >= ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roomNum);
            ps.setTimestamp(2, Timestamp.valueOf(ci.atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(co.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /**
     * Tạo 1 booking cho N phòng.
     * - Book_by = currentUsername; By_role = currentUsername (tạm theo yêu cầu)
     * - Book_contact = bookContact (text input của khách)
     * - Kiểm tra trùng lịch từng phòng trước khi insert
     * @return Booking_id nếu thành công, null nếu conflict hoặc lỗi
     */
    public static String createWithRooms(
            List<String> roomNums,
            LocalDate plannedIn,
            LocalDate plannedOut,
            int depositAmount,
            String paymentMethod,
            int bookingStatus,
            String currentUsername, // -> Book_by & By_role
            String bookContact      // -> Book_contact (text khách)
    ) {
        final String nextIdSql = "SELECT dbo.fn_GenerateNextBookingID() AS NextId";
        final String insertBookingSql =
            "INSERT INTO dbo.Booking_Management " +
            "(Booking_id, Deposit_amount, Payment_method, Booking_status, " +
            " Booking_date, Planned_checkin_date, Planned_checkout_date, " +
            " Book_by, Book_contact, Created_at, Updated_at, By_role) " +
            "VALUES (?,?,?,?, GETDATE(), ?, ?, ?, ?, GETDATE(), GETDATE(), ?)";
        final String linkRoomSql =
            "INSERT INTO dbo.Booking_Room (Booking_id, Room_id) " +
            "SELECT ?, rm.Room_id FROM dbo.Room_Management rm " +
            "WHERE LTRIM(RTRIM(rm.Room_num)) = LTRIM(RTRIM(?))";

        try (Connection c = DButil.getConnection()) {
            c.setAutoCommit(false);

            // 1) Conflict check tất cả phòng
            for (String rn : roomNums) {
                if (hasConflictTx(c, rn, plannedIn, plannedOut)) {
                    c.rollback();
                    return null;
                }
            }

            // 2) New id
            String bookingId;
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(nextIdSql)) {
                rs.next(); bookingId = rs.getString("NextId");
            }

            // 3) Insert header
            try (PreparedStatement ps = c.prepareStatement(insertBookingSql)) {
                ps.setString(1, bookingId);
                ps.setInt(2, depositAmount);
                ps.setString(3, paymentMethod);
                ps.setInt(4, bookingStatus);
                ps.setTimestamp(5, Timestamp.valueOf(plannedIn.atStartOfDay()));
                ps.setTimestamp(6, Timestamp.valueOf(plannedOut.atStartOfDay()));
                ps.setString(7, currentUsername); // Book_by
                ps.setString(8, bookContact);     // Book_contact
                ps.setString(9, currentUsername); // By_role (tạm = username)
                ps.executeUpdate();
            }

            // 4) Link rooms
            try (PreparedStatement ps = c.prepareStatement(linkRoomSql)) {
                for (String rn : roomNums) {
                    ps.setString(1, bookingId);
                    ps.setString(2, rn);
                    if (ps.executeUpdate() != 1) {
                        c.rollback();
                        throw new SQLException("Room_num not found: " + rn);
                    }
                }
            }

            c.commit();
            return bookingId;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
