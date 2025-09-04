package HotelApp.repository;

import HotelApp.db.DButil;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public final class RoomRepository {
    private RoomRepository(){}

    
    public static boolean updateStatusByRoomNumber(String roomNum, int status){
        final String sql = "UPDATE dbo.Room_Management SET Room_status=? WHERE LTRIM(RTRIM(Room_num)) = LTRIM(RTRIM(?))";
        try (Connection c = DButil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setString(2, roomNum);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public static List<Map<String,Object>> searchRooms(Integer capacity, String type,
                                                       LocalDate ci, LocalDate co) {
        // Gợi ý: lọc theo RoomType_Amenity + Room_Management.
        // Không chặn double-booking. Kiểm tra cứng nằm ở BookingRepository.
        StringBuilder sb = new StringBuilder();
        sb.append("""
            SELECT rm.Room_num AS room_num,
                   rta.Room_capacity AS capacity,
                   rm.Room_quality AS type,
                   CAST(NULL AS datetime) AS near_in,
                   CAST(NULL AS datetime) AS near_out,
                   rm.Room_price AS price,
                   CASE WHEN rm.Room_status = 0 THEN 'Available' 
                        WHEN rm.Room_status = 1 THEN 'Occupied'
                        WHEN rm.Room_status = 2 THEN 'Cleaning' 
                        ELSE 'Unknown' END AS availability
            FROM dbo.Room_Management rm
            JOIN dbo.RoomType_Amenity rta
              ON rta.Room_category = rm.Room_category
             AND rta.Room_quality = rm.Room_quality
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();
        if (capacity != null) { sb.append(" AND rta.Room_capacity >= ?"); params.add(capacity); }
        if (type != null && !type.isBlank()) { sb.append(" AND rm.Room_quality = ?"); params.add(type); }

        sb.append(" ORDER BY rm.Room_num");

        List<Map<String,Object>> rows = new ArrayList<>();
        try (Connection c = DButil.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {

            for (int i=0;i<params.size();i++) ps.setObject(i+1, params.get(i));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> m = new HashMap<>();
                    m.put("room_num", rs.getString("room_num"));
                    m.put("capacity", rs.getInt("capacity"));
                    m.put("type", rs.getString("type"));
                    m.put("near_in", null);   // có thể tính “nearest booking” nếu cần
                    m.put("near_out", null);
                    m.put("price", rs.getDouble("price"));
                    m.put("availability", rs.getString("availability"));
                    rows.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
