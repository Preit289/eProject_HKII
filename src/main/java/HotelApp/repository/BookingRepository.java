package HotelApp.repository;

import HotelApp.BookingController;
import HotelApp.BookingFormController;
import HotelApp.db.DButil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {

    private Connection getConn() throws SQLException { return DButil.getConnection(); }

   public List<BookingController.BookingVM> searchVM(String keyword){
    String sql = """
        SELECT b.Booking_id, b.Book_by, b.Book_contact,
               b.Deposit_amount, b.Payment_method,
               (SELECT COUNT(*) FROM Booking_Room br WHERE br.Booking_id = b.Booking_id) AS Rooms
        FROM Booking_Management b
        ORDER BY b.Booking_id
    """;
    List<BookingController.BookingVM> list = new ArrayList<>();
    try (Connection cn = DButil.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while(rs.next()){
            list.add(new BookingController.BookingVM(
                rs.getString("Booking_id"),
                rs.getString("Book_by"),
                rs.getString("Book_contact"),
                rs.getInt("Deposit_amount"),
                rs.getString("Payment_method"),
                rs.getInt("Rooms")
            ));
        }
    } catch (Exception e){ e.printStackTrace(); }
    return list;
}


    // Detail
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

    public String createBooking(BookingFormController.BookingDTO b){
        String newId = nextBookingId();
        String sql =
          "INSERT INTO Booking_Management(Booking_id, Deposit_amount, Payment_method, Booking_status," +
          " Booking_date, Planned_checkin_date, Planned_checkout_date, Book_by, Book_contact," +
          " Created_at, Updated_at, By_role) " +
          "VALUES(?, ?, ?, 2, GETDATE(), GETDATE(), DATEADD(day,1,GETDATE()), ?, ?, GETDATE(), GETDATE(), 'admin')";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1,newId);
            ps.setInt(2, b.deposit());
            ps.setString(3, b.payment());
            ps.setString(4, b.booker());
            ps.setString(5, b.phone());
            ps.executeUpdate();
            return newId;
        }catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    public boolean updateBooking(BookingFormController.BookingDTO b){
        String sql = "UPDATE Booking_Management SET Deposit_amount=?, Payment_method=?, Book_by=?, Book_contact=?, Updated_at=GETDATE() WHERE Booking_id=?";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setInt(1, b.deposit());
            ps.setString(2, b.payment());
            ps.setString(3, b.booker());
            ps.setString(4, b.phone());
            ps.setString(5, b.id());
            return ps.executeUpdate() > 0;
        }catch(Exception e){ e.printStackTrace(); }
        return false;
    }

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

    // Rooms
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

    public boolean addRoomToBooking(String bookingId, String roomId){
        String sql = "INSERT INTO Booking_Room(Booking_id, Room_id) VALUES(?, ?)";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, bookingId); ps.setString(2, roomId);
            return ps.executeUpdate() > 0;
        }catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    public boolean removeRoomFromBooking(String bookingId, String roomId){
        String sql = "DELETE FROM Booking_Room WHERE Booking_id=? AND Room_id=?";
        try(Connection cn = getConn(); PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, bookingId); ps.setString(2, roomId);
            return ps.executeUpdate() > 0;
        }catch(Exception e){ e.printStackTrace(); }
        return false;
    }

    public List<BookingFormController.RoomPickVM> pickFreeRoomsVM(){
        String sql = "SELECT Room_id, Room_num, Room_category, Room_quality FROM Room_Management WHERE Room_status=0 ORDER BY Room_num";
        List<BookingFormController.RoomPickVM> list = new ArrayList<>();
        try(Connection cn = getConn(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)){
            while(rs.next()){
                String id = rs.getString(1);
                String label = rs.getString(2)+" • "+rs.getString(3)+" • "+rs.getString(4);
                list.add(new BookingFormController.RoomPickVM(id,label));
            }
        }catch(Exception e){ e.printStackTrace(); }
        return list;
    }

    private String nextBookingId(){
        String sql = "SELECT dbo.fn_GenerateNextBookingID()";
        try(Connection cn = getConn(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)){
            if(rs.next()) return rs.getString(1);
        }catch(Exception e){ e.printStackTrace(); }
        return "B" + System.currentTimeMillis();
    }
}
