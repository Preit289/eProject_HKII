package HotelApp.repository;

import HotelApp.db.DButil;
import HotelApp.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public final class RoomRepository {
    private RoomRepository(){}

    // Lấy tất cả phòng từ DB
    public static ObservableList<Room> getAll() {
        ObservableList<Room> list = FXCollections.observableArrayList();
        String sql = "SELECT Room_id, Room_category, Room_num, Room_quality, Room_price, Room_status " +
                     "FROM dbo.Room_Management";
        try (Connection cn = DButil.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                list.add(new Room(
                    rs.getString("Room_id"),
                    rs.getString("Room_category"),
                    rs.getString("Room_num"),
                    rs.getString("Room_quality"),
                    rs.getDouble("Room_price"),
                    rs.getString("Room_status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Tìm phòng theo Room_num
    public static Room findByRoomNumber(String roomNumber) {
        String sql = "SELECT Room_id, Room_category, Room_num, Room_quality, Room_price, Room_status " +
                     "FROM dbo.Room_Management " +
                     "WHERE LTRIM(RTRIM(Room_num)) = LTRIM(RTRIM(?))";
        try (Connection cn = DButil.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, roomNumber);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Room(
                        rs.getString("Room_id"),
                        rs.getString("Room_category"),
                        rs.getString("Room_num"),
                        rs.getString("Room_quality"),
                        rs.getDouble("Room_price"),
                        rs.getString("Room_status")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    public static void updateStatusByRoomNumber(String roomNum, int status) {
    final String sql = "UPDATE dbo.Room_Management SET Room_status=? WHERE LTRIM(RTRIM(Room_num))=LTRIM(RTRIM(?))";
    try (var cn = DButil.getConnection(); var st = cn.prepareStatement(sql)) {
        st.setInt(1, status);
        st.setString(2, roomNum);
        st.executeUpdate();
    } catch (Exception e) { throw new RuntimeException(e); }
}

    // Lấy giá phòng theo Room_num
    public static double getPriceByRoomNumber(String roomNumber) {
        String sql = "SELECT Room_price FROM dbo.Room_Management " +
                     "WHERE LTRIM(RTRIM(Room_num)) = LTRIM(RTRIM(?))";
        try (Connection cn = DButil.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, roomNumber);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getDouble("Room_price");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        throw new IllegalArgumentException("Room not found: " + roomNumber);
    }

    // Lưu phòng mới (sinh Room_id bằng function trong DB)
    public static void save(Room room) {
        String getIdSql = "SELECT dbo.fn_GenerateNextRoomID() AS NewId";
        String insertSql = "INSERT INTO dbo.Room_Management " +
                           "(Room_id, Room_category, Room_num, Room_quality, Room_price, Room_status) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = DButil.getConnection()) {
            String newId;
            try (Statement s = cn.createStatement();
                 ResultSet rs = s.executeQuery(getIdSql)) {
                rs.next();
                newId = rs.getString("NewId");
            }
            try (PreparedStatement st = cn.prepareStatement(insertSql)) {
                st.setString(1, newId);
                st.setString(2, room.getCategory());
                st.setString(3, room.getRoomNumber());
                st.setString(4, room.getQuality());
                st.setDouble(5, room.getPrice());
                st.setString(6, room.getStatus());
                st.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
