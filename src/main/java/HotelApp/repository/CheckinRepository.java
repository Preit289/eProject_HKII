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

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

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

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + phone + "%"); // partial search allowed
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

}
