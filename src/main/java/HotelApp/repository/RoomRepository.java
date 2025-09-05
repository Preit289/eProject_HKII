package HotelApp.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import HotelApp.db.DButil;
import HotelApp.model.Room;

public class RoomRepository {

    public static String generateNextRoomId() {
        String sql = "SELECT dbo.fn_GenerateNextRoomID()";
        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = """
                    SELECT rm.Room_id, rm.Room_num, rm.Room_category, rm.Room_quality, 
                           rm.Room_price, rm.Room_status, ra.Room_amenity, ra.Room_capacity 
                    FROM Room_Management rm
                    JOIN RoomType_Amenity ra 
                    ON rm.Room_category = ra.Room_category 
                    AND rm.Room_quality = ra.Room_quality
                    """;

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String roomId = rs.getString("Room_id");
                String roomNum = rs.getString("Room_num");
                String category = rs.getString("Room_category");
                String quality = rs.getString("Room_quality");
                int price = rs.getInt("Room_price");
                String amenities = rs.getString("Room_amenity");
                int capacity = rs.getInt("Room_capacity");

                String status = switch (rs.getInt("Room_status")) {
                    case 0 ->
                        "Available";
                    case 1 ->
                        "Occupied";
                    case 2 ->
                        "Cleaning";
                    default ->
                        "Unknown";
                };

                rooms.add(new Room(roomId, roomNum, category, quality, price, status, amenities, capacity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    private static int convertStatusToInt(String status) {
        return switch (status) {
            case "Available" ->
                0;
            case "Occupied" ->
                1;
            case "Cleaning" ->
                2;
            default ->
                throw new IllegalArgumentException("Invalid status: " + status);
        };
    }

    public static boolean addRoom(Room room) {
        String sql = """
                    INSERT INTO Room_Management (Room_id, Room_num, Room_category, Room_quality, Room_price, Room_status)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomId());
            ps.setString(2, room.getRoomNumber());
            ps.setString(3, room.getCategory());
            ps.setString(4, room.getQuality());
            ps.setInt(5, room.getPrice());
            ps.setInt(6, convertStatusToInt(room.getStatus()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateRoom(Room room) {
        String sql = """
                    UPDATE Room_Management 
                    SET Room_num = ?, Room_category = ?, Room_quality = ?, 
                        Room_price = ?, Room_status = ?
                    WHERE Room_id = ?
                    """;

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getCategory());
            ps.setString(3, room.getQuality());
            ps.setInt(4, room.getPrice());
            ps.setInt(5, convertStatusToInt(room.getStatus()));
            ps.setString(6, room.getRoomId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteRoom(String roomId) {
        String sql = "DELETE FROM Room_Management WHERE Room_id = ?";

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static RoomTypeInfo getRoomTypeInfo(String category, String quality) {
        String sql = "SELECT Room_amenity, Room_capacity FROM RoomType_Amenity WHERE Room_category = ? AND Room_quality = ?";

        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            ps.setString(2, quality);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String amenities = rs.getString("Room_amenity");
                    int capacity = rs.getInt("Room_capacity");
                    return new RoomTypeInfo(amenities, capacity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
