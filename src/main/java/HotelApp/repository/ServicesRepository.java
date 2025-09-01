package HotelApp.repository;

import HotelApp.db.DButil;
import HotelApp.model.Services;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesRepository {

    public List<Services> getAllServices() {
        List<Services> services = new ArrayList<>();
        String query = "SELECT * FROM Service_Management";

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Services service = new Services(
                        rs.getString("Service_id"),
                        rs.getString("Service_name"),
                        rs.getInt("Service_price"),
                        rs.getString("Service_description")
                );
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public boolean addService(Services service) {
        String query = "INSERT INTO Service_Management (Service_id, Service_name, Service_price, Service_description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, service.getServiceId());
            pstmt.setString(2, service.getServiceName());
            pstmt.setInt(3, service.getServicePrice());
            pstmt.setString(4, service.getServiceDescription());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateService(Services service) {
        String query = "UPDATE Service_Management SET Service_name = ?, Service_price = ?, Service_description = ? WHERE Service_id = ?";

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, service.getServiceName());
            pstmt.setInt(2, service.getServicePrice());
            pstmt.setString(3, service.getServiceDescription());
            pstmt.setString(4, service.getServiceId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteService(String serviceId) {
        String query = "DELETE FROM Service_Management WHERE Service_id = ?";

        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, serviceId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getNextServiceId() {
        String query = "SELECT dbo.fn_GenerateNextServiceID()";
        try (Connection conn = DButil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
