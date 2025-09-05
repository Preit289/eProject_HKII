package HotelApp.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import HotelApp.db.DButil;
import HotelApp.model.RoomTypeAmenity;

public class RoomTypeAmenityRepository {

    public static List<RoomTypeAmenity> getAll() {
        List<RoomTypeAmenity> roomTypes = new ArrayList<>();
        String sql = "SELECT * FROM RoomType_Amenity";

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("Room_category");
                String quality = rs.getString("Room_quality");
                int capacity = rs.getInt("Room_capacity");
                String amenities = rs.getString("Room_amenity");

                roomTypes.add(new RoomTypeAmenity(category, quality, capacity, amenities));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomTypes;
    }

    public static boolean add(RoomTypeAmenity roomType) {
        String sql = """
                    INSERT INTO RoomType_Amenity (Room_category, Room_quality, Room_capacity, Room_amenity)
                    VALUES (?, ?, ?, ?)
                    """;

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomType.getCategory());
            ps.setString(2, roomType.getQuality());
            ps.setInt(3, roomType.getCapacity());
            ps.setString(4, roomType.getAmenities());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(RoomTypeAmenity roomType) {
        String sql = """
                    UPDATE RoomType_Amenity 
                    SET Room_capacity = ?, Room_amenity = ?
                    WHERE Room_category = ? AND Room_quality = ?
                    """;

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomType.getCapacity());
            ps.setString(2, roomType.getAmenities());
            ps.setString(3, roomType.getCategory());
            ps.setString(4, roomType.getQuality());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(String category, String quality) {
        String sql = "DELETE FROM RoomType_Amenity WHERE Room_category = ? AND Room_quality = ?";

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            ps.setString(2, quality);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
