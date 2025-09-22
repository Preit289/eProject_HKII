package HotelApp.repository;

import HotelApp.BookingController;
import HotelApp.BookingFormController;
import HotelApp.db.DButil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {

    private Connection getConn() throws SQLException { 
        return DButil.getConnection(); 
    }

    // Lấy danh sách booking để hiển thị
    // Lấy danh sách booking để hiển thị
public List<BookingController.BookingVM> searchVM(String keyword){
    String sql = "SELECT b.Booking_id, b.Book_by, b.Book_contact, " +
                 "b.Deposit_amount, b.Payment_method, " +
                 "(SELECT COUNT(*) FROM Booking_Room br WHERE br.Booking_id = b.Booking_id) AS Rooms, " +
                 "b.Created_at, b.Planned_checkin_date " +  // thêm 2 cột
                 "FROM Booking_Management b " +
                 "WHERE b.Book_by LIKE ? OR b.Book_contact LIKE ? " +  // có tìm kiếm
                 "ORDER BY b.Booking_id";
    List<BookingController.BookingVM> list = new ArrayList<>();
    try (Connection cn = getConn();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        String kw = "%" + keyword + "%";
        ps.setString(1, kw);
        ps.setString(2, kw);
        try (ResultSet rs = ps.executeQuery()) {
            while(rs.next()){
                list.add(new BookingController.BookingVM(
                        rs.getString("Booking_id"),
                        rs.getString("Book_by"),
                        rs.getString("Book_contact"),
                        rs.getInt("Deposit_amount"),
                        rs.getString("Payment_method"),
                        rs.getInt("Rooms"),
                        rs.getTimestamp("Created_at") != null ? rs.getTimestamp("Created_at").toLocalDateTime() : null,
                        rs.getTimestamp("Planned_checkin_date") != null ? rs.getTimestamp("Planned_checkin_date").toLocalDateTime() : null
                ));
            }
        }
    } catch (Exception e){ e.printStackTrace(); }
    return list;
}

    // Lấy booking chi tiết
    public BookingFormController.BookingDTO getById(String id){
        String sql = "SELECT Booking_id, Book_by, Book_contact, Deposit_amount, Payment_method " +
                     "FROM Booking_Management WHERE Booking_id=?";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new BookingFormController.BookingDTO(
                            rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getInt(4), rs.getString(5)
                    );
                }
            }
        }catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    // Tạo booking (có ngày checkin/checkout)
    public String createBooking(BookingFormController.BookingDTO b, LocalDate checkin, LocalDate checkout){
        String newId = nextBookingId();
        String sql =
          "INSERT INTO Booking_Management(Booking_id, Deposit_amount, Payment_method, Booking_status," +
          " Booking_date, Planned_checkin_date, Planned_checkout_date, Book_by, Book_contact," +
          " Created_at, Updated_at, By_role) " +
          "VALUES(?, ?, ?, 2, GETDATE(), ?, ?, ?, ?, GETDATE(), GETDATE(), 'admin')";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1,newId);
            ps.setInt(2, b.deposit());
            ps.setString(3, b.payment());
            ps.setTimestamp(4, Timestamp.valueOf(checkin.atStartOfDay()));
            ps.setTimestamp(5, Timestamp.valueOf(checkout.atStartOfDay()));
            ps.setString(6, b.booker());
            ps.setString(7, b.phone());
            ps.executeUpdate();
            return newId;
        }catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    // Update booking (có ngày)
    public boolean updateBooking(BookingFormController.BookingDTO b, LocalDate checkin, LocalDate checkout){
        String sql = "UPDATE Booking_Management SET Deposit_amount=?, Payment_method=?, Book_by=?, Book_contact=?," +
                     " Planned_checkin_date=?, Planned_checkout_date=?, Updated_at=GETDATE() WHERE Booking_id=?";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setInt(1, b.deposit());
            ps.setString(2, b.payment());
            ps.setString(3, b.booker());
            ps.setString(4, b.phone());
            ps.setTimestamp(5, Timestamp.valueOf(checkin.atStartOfDay()));
            ps.setTimestamp(6, Timestamp.valueOf(checkout.atStartOfDay()));
            ps.setString(7, b.id());
            return ps.executeUpdate() > 0;
        }catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    // Xóa booking
    public boolean deleteBooking(String id){
        try(Connection cn = getConn()){
            cn.setAutoCommit(false);
            try(PreparedStatement p1 = cn.prepareStatement("DELETE FROM Booking_Room WHERE Booking_id=?");
                PreparedStatement p2 = cn.prepareStatement("DELETE FROM Booking_Management WHERE Booking_id=?")){
                p1.setString(1,id); p1.executeUpdate();
                p2.setString(1,id); int n = p2.executeUpdate();
                cn.commit();
                return n>0;
            }catch(Exception ex){ cn.rollback(); throw ex; }
            finally { cn.setAutoCommit(true); }
        }catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    // Lấy danh sách phòng đã add vào booking
    public List<BookingFormController.RoomVM> getRoomsVM(String bookingId){
        String sql = "SELECT r.Room_id, r.Room_num, r.Room_category, r.Room_quality, r.Room_price " +
                     "FROM Booking_Room br JOIN Room_Management r ON r.Room_id=br.Room_id " +
                     "WHERE br.Booking_id=? ORDER BY r.Room_num";
        List<BookingFormController.RoomVM> list = new ArrayList<>();
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, bookingId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(new BookingFormController.RoomVM(
                            rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getInt(5)
                    ));
                }
            }
        }catch(Exception e){ e.printStackTrace(); }
        return list;
    }

    // Thêm phòng vào booking
    public boolean addRoomToBooking(String bookingId, String roomId){
        String sql = "INSERT INTO Booking_Room(Booking_id, Room_id) VALUES(?, ?)";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, bookingId); ps.setString(2, roomId);
            return ps.executeUpdate() > 0;
        }catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    // Xóa phòng khỏi booking
    public boolean removeRoomFromBooking(String bookingId, String roomId){
        String sql = "DELETE FROM Booking_Room WHERE Booking_id=? AND Room_id=?";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, bookingId); ps.setString(2, roomId);
            return ps.executeUpdate() > 0;
        }catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    // Lấy danh sách phòng trống trong khoảng ngày
    public List<BookingFormController.RoomPickVM> pickFreeRoomsVM(LocalDate checkin, LocalDate checkout){
        String sql = "SELECT r.Room_id, r.Room_num, r.Room_category, r.Room_quality " +
                     "FROM Room_Management r " +
                     "WHERE r.Room_status = 0 AND NOT EXISTS ( " +
                     "  SELECT 1 FROM Booking_Room br JOIN Booking_Management bm ON bm.Booking_id = br.Booking_id " +
                     "  WHERE br.Room_id = r.Room_id " +
                     "    AND bm.Booking_status >= 2 " +
                     "    AND (bm.Planned_checkin_date < ? AND bm.Planned_checkout_date > ?) " +
                     ") ORDER BY r.Room_num";
        List<BookingFormController.RoomPickVM> list = new ArrayList<>();
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setTimestamp(1, Timestamp.valueOf(checkout.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(checkin.atStartOfDay()));
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    String id = rs.getString(1);
                    String label = rs.getString(2)+" • "+rs.getString(3)+" • "+rs.getString(4);
                    list.add(new BookingFormController.RoomPickVM(id,label));
                }
            }
        }catch(Exception e){ e.printStackTrace(); }
        return list;
    }

    // Kiểm tra phòng có trống không
    public boolean isRoomAvailable(String roomId, LocalDate checkin, LocalDate checkout){
        String sql = "SELECT COUNT(*) " +
                     "FROM Booking_Management bm " +
                     "JOIN Booking_Room br ON bm.Booking_id = br.Booking_id " +
                     "WHERE br.Room_id = ? " +
                     "  AND bm.Booking_status >= 2 " +
                     "  AND (bm.Planned_checkin_date < ? AND bm.Planned_checkout_date > ?)";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, roomId);
            ps.setTimestamp(2, Timestamp.valueOf(checkout.atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(checkin.atStartOfDay()));
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) return rs.getInt(1) == 0;
            }
        }catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    // Lấy planned checkin/checkout
    public LocalDate[] getPlannedDates(String bookingId){
        String sql = "SELECT Planned_checkin_date, Planned_checkout_date FROM Booking_Management WHERE Booking_id=?";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, bookingId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    LocalDate ci = rs.getTimestamp(1).toLocalDateTime().toLocalDate();
                    LocalDate co = rs.getTimestamp(2).toLocalDateTime().toLocalDate();
                    return new LocalDate[]{ci, co};
                }
            }
        }catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    // Sinh BookingID mới
    private String nextBookingId(){
        String sql = "SELECT dbo.fn_GenerateNextBookingID()";
        try(Connection cn = getConn(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)){
            if(rs.next()) return rs.getString(1);
        }catch(Exception e){ e.printStackTrace(); }
        return "B" + System.currentTimeMillis();
    }
}
