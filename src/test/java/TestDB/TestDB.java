package TestDB;

import HotelApp.db.DButil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestDB {

    public static void main(String[] args) {
        String sql = "select * from Booking_Management";
        try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("Connect successfully !");
            System.out.println("+--------------------+");
            System.out.println("|Sample queried data:|");
            System.out.println("+--------------------+");
            System.out.println("+--------------------------------------------------------------------+");
            System.out.printf("| %-20s | %-20s | %-20s |%n", "Col2", "Col3", "Col4");
            System.out.println("+--------------------------------------------------------------------+");
            while (rs.next()) {
                System.out.printf("| %-20s | %-20s | %-20s |%n",
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4));
            }
            System.out.println("+--------------------------------------------------------------------+");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
