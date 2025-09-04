package HotelApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML private StackPane content;
    @FXML private Label lblTitle;     // nếu bạn muốn hiển thị title động
    @FXML private ImageView imgLogo;  // logo placeholder

    @FXML
    private void initialize() {
        // mở Booking mặc định khi khởi động
        load("HotelApp/Booking.fxml", "Booking");
    }

   private void load(String absPath, String title) {
    try {
        var url = getClass().getResource("/" + absPath); // <- thêm dấu '/'
        if (url == null) throw new IllegalStateException("Not found: /" + absPath);
        javafx.scene.Parent view = javafx.fxml.FXMLLoader.load(url);
        content.getChildren().setAll(view);
    } catch (Exception e) {
        e.printStackTrace(); // tạm để thấy lỗi nếu còn sai
    }
}


    // Sidebar navigation
    @FXML private void openBooking()  { load("HotelApp/Booking.fxml",  "Booking"); }
    @FXML private void openCheckin()  { load("HotelApp/Checkin.fxml",  "Check in"); }
    @FXML private void openCheckout() { load("HotelApp/Checkout.fxml", "Check out"); }
    @FXML private void openCancel()   { load("HotelApp/Cancel.fxml",   "Cancel booking"); }
    @FXML private void openService()  { load("HotelApp/Services.fxml", "Service"); }
    @FXML private void openRoom()     { load("HotelApp/RoomMgmt.fxml", "Room"); }

    // Logo + text click → Dashboard
    @FXML private void openDashboard() {
        load("HotelApp/Dashboard.fxml", "Dashboard");
    }
}
