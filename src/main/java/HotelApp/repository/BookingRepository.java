package HotelApp.repository;

import HotelApp.db.DButil;
import HotelApp.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public final class BookingRepository {
    private BookingRepository(){}

    // Hiển thị tất cả booking (kể cả chưa gán phòng)
    public static ObservableList<Booking> getAll() {
        ObservableList<Booking> list = FXCollections.observableArrayList();
        final String sql =
            "SELECT bm.Book_by, CAST(bm.Checkin_date AS date) ci, CAST(bm.Checkout_date AS date) co, " +
            "       rm.Room_num, COALESCE(rm.Room_price,0) price, " +
            "       COALESCE(DATEDIFF(DAY,bm.Checkin_date,bm.Checkout_date),0) nights " +
            "FROM dbo.Booking_Management bm " +
            "LEFT JOIN dbo.Booking_Room br ON br.Booking_id = bm.Booking_id " +
            "LEFT JOIN dbo.Room_Management rm ON rm.Room_id = br.Room_id " +
            "ORDER BY bm.Created_at DESC";
        try (Connection cn = DButil.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                String guest = rs.getString("Book_by");
                String room  = rs.getString("Room_num");
                LocalDate ci = rs.getDate("ci").toLocalDate();
                LocalDate co = rs.getDate("co").toLocalDate();
                int nights   = Math.max(1, rs.getInt("nights"));
                double total = rs.getDouble("price") * nights;

                Booking b = new Booking(guest, room == null ? "" : room, ci, co, null);
                b.setPrice(total);
                list.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Kiểm tra trùng lịch (overlap) cùng Room_num
    public static boolean hasConflict(String roomNum, LocalDate ci, LocalDate co) {
        final String sql =
            "SELECT 1 " +
            "FROM dbo.Booking_Management bm " +
            "JOIN dbo.Booking_Room br ON br.Booking_id = bm.Booking_id " +
            "JOIN dbo.Room_Management rm ON rm.Room_id = br.Room_id " +
            "WHERE LTRIM(RTRIM(rm.Room_num)) = LTRIM(RTRIM(?)) " +
            "  AND NOT (CAST(bm.Checkout_date AS date) <= ? OR CAST(bm.Checkin_date AS date) >= ?)";
        try (Connection cn = DButil.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, roomNum);
            st.setDate(2, Date.valueOf(ci));
            st.setDate(3, Date.valueOf(co));
            try (ResultSet rs = st.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return true; }
    }
    
    public static void updateStatus(String bookingId, int status) {
    final String sql = "UPDATE dbo.Booking_Management SET Booking_status=?, Updated_at=GETDATE() WHERE Booking_id=?";
    try (var cn = DButil.getConnection(); var st = cn.prepareStatement(sql)) {
        st.setInt(1, status);
        st.setString(2, bookingId);
        st.executeUpdate();
    } catch (Exception e) { throw new RuntimeException(e); }
}

    // Lưu booking; gán phòng nếu người dùng nhập Room_num
    public static void save(Booking b) {
        final String qNewId = "SELECT dbo.fn_GenerateNextBookingID() AS NewId";
        final String insBM  =
            "INSERT INTO dbo.Booking_Management " +
            "(Booking_id,Deposit_amount,Payment_method,Booking_status,Booking_date," +
            " Checkin_date,Checkout_date,Book_by,Book_contact,Created_at,Updated_at) " +
            "VALUES(?,0,'Cash',2,GETDATE(),?,?,?,?,GETDATE(),GETDATE())";
        final String qRoom  = "SELECT Room_id FROM dbo.Room_Management " +
                              "WHERE LTRIM(RTRIM(Room_num))=LTRIM(RTRIM(?))";
        final String insBR  = "INSERT INTO dbo.Booking_Room(Booking_id,Room_id) VALUES(?,?)";

        Connection cn = null;
        try {
            cn = DButil.getConnection();
            cn.setAutoCommit(false);

            String roomNum = b.getRoomNumber() == null ? "" : b.getRoomNumber().trim();
            String roomId  = null;

            if (!roomNum.isEmpty()) {
                if (hasConflict(roomNum, b.getCheckInDate(), b.getCheckOutDate())) {
                    throw new SQLException("Room schedule conflict: " + roomNum);
                }
                try (PreparedStatement st = cn.prepareStatement(qRoom)) {
                    st.setString(1, roomNum);
                    try (ResultSet rs = st.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Room not found: " + roomNum);
                        roomId = rs.getString("Room_id");
                    }
                }
            }

            String bookingId;
            try (Statement st = cn.createStatement();
                 ResultSet rs = st.executeQuery(qNewId)) {
                rs.next();
                bookingId = rs.getString("NewId");
            }

            try (PreparedStatement st = cn.prepareStatement(insBM)) {
                st.setString(1, bookingId);
                st.setDate(2, Date.valueOf(b.getCheckInDate()));
                st.setDate(3, Date.valueOf(b.getCheckOutDate()));
                st.setString(4, b.getGuestName());
                st.setString(5, "");
                st.executeUpdate();
            }

            if (roomId != null) {
                try (PreparedStatement st = cn.prepareStatement(insBR)) {
                    st.setString(1, bookingId);
                    st.setString(2, roomId);
                    st.executeUpdate();
                }
            }

            cn.commit();
        } catch (SQLException e) {
            try { if (cn != null) cn.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException(e);
        } finally {
            try { if (cn != null) cn.close(); } catch (SQLException ignore) {}
        }
    }
}
