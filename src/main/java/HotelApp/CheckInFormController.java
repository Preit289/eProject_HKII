////package HotelApp;
////
////import javafx.fxml.FXML;
////import javafx.scene.control.*;
////import javafx.scene.layout.BorderPane;
////import javafx.beans.property.*;
////import javafx.collections.FXCollections;
////import javafx.event.ActionEvent;
////import javafx.stage.Stage;
////
////public class CheckInFormController {
////
////    @FXML private BorderPane rootPane;
////    @FXML private Label lblTitle;
////    @FXML private TextField txtBooker;
////    @FXML private TextField txtPhone;
////    @FXML private ComboBox<String> cbPayment;
////    @FXML private TextField txtDeposit;
////    @FXML private Button btnAddRoom;
////    @FXML private Button btnRemoveRoom;
////    @FXML private TableView<RoomVM> tblRooms;
////    @FXML private TableColumn<RoomVM, String> colRoomNum;
////    @FXML private TableColumn<RoomVM, String> colCategory;
////    @FXML private TableColumn<RoomVM, String> colQuality;
////    @FXML private TableColumn<RoomVM, Number> colPrice;
////    @FXML private Button btnDelete;
////    @FXML private Button btnSave;
////    @FXML private Button btnClose;
////
////    // Simple record for room view model
////    public record RoomVM(String roomNumber, String category, String quality, double price) {}
////
////    @FXML
////    private void initialize() {
////        // Set up ComboBox with payment options
////        cbPayment.setItems(FXCollections.observableArrayList("Cash", "Credit Card", "Debit Card"));
////
////        // Configure table columns
////        colRoomNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().roomNumber()));
////        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category()));
////        colQuality.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().quality()));
////        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().price()));
////
////        // Initialize table with empty data
////        tblRooms.setItems(FXCollections.observableArrayList());
////    }
////
////    @FXML
////    private void onAddRoom(ActionEvent event) {
////        // TODO: Implement logic to add a room
////        System.out.println("Add Room clicked");
////    }
////
////    @FXML
////    private void onRemoveRoom(ActionEvent event) {
////        // TODO: Implement logic to remove selected room
////        System.out.println("Remove Room clicked");
////    }
////
////    @FXML
////    private void onDelete(ActionEvent event) {
////        // TODO: Implement logic to delete booking
////        System.out.println("Delete clicked");
////    }
////
////    @FXML
////    private void onSave(ActionEvent event) {
////        // TODO: Implement logic to save booking
////        System.out.println("Save clicked");
////    }
////
////    @FXML
////    private void onClose(ActionEvent event) {
////        // Close the window
////        Stage stage = (Stage) btnClose.getScene().getWindow();
////        stage.close();
////    }
////}
//
//package HotelApp;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.layout.BorderPane;
//import javafx.beans.property.*;
//import javafx.collections.FXCollections;
//import javafx.event.ActionEvent;
//import javafx.stage.Stage;
//
//public class CheckInFormController {
//
//    @FXML private BorderPane rootPane;
//    @FXML private Label lblTitle;
//    @FXML private TextField txtBooker;
//    @FXML private TextField txtPhone;
//    @FXML private ComboBox<String> cbPayment;
//    @FXML private TextField txtDeposit;
//    @FXML private Button btnAddRoom;
//    @FXML private Button btnRemoveRoom;
//    @FXML private TableView<RoomVM> tblRooms;
//    @FXML private TableColumn<RoomVM, String> colRoomNum;
//    @FXML private TableColumn<RoomVM, String> colCategory;
//    @FXML private TableColumn<RoomVM, String> colQuality;
//    @FXML private TableColumn<RoomVM, Number> colPrice;
//    @FXML private TableColumn<RoomVM, String> colCustomer;
//    @FXML private TableColumn<RoomVM, String> colServices;
//    @FXML private Button btnDelete;
//    @FXML private Button btnSave;
//    @FXML private Button btnClose;
//
//    // Updated record for room view model with customer and services
//    public record RoomVM(String roomNumber, String category, String quality, double price, 
//                        String customer, String services) {}
//
//    @FXML
//    private void initialize() {
//        // Set up ComboBox with payment options
//        cbPayment.setItems(FXCollections.observableArrayList("Cash", "Credit Card", "Debit Card"));
//
//        // Configure table columns
//        colRoomNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().roomNumber()));
//        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category()));
//        colQuality.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().quality()));
//        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().price()));
//        colCustomer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().customer()));
//        colServices.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().services()));
//
//        // Initialize table with empty data
//        tblRooms.setItems(FXCollections.observableArrayList());
//    }
//
//    @FXML
//    private void onAddRoom(ActionEvent event) {
//        // TODO: Implement logic to add a room
//        System.out.println("Add Room clicked");
//    }
//
//    @FXML
//    private void onRemoveRoom(ActionEvent event) {
//        // TODO: Implement logic to remove selected room
//        System.out.println("Remove Room clicked");
//    }
//
//    @FXML
//    private void onDelete(ActionEvent event) {
//        // TODO: Implement logic to delete booking
//        System.out.println("Delete clicked");
//    }
//
//    @FXML
//    private void onSave(ActionEvent event) {
//        // TODO: Implement logic to save booking
//        System.out.println("Save clicked");
//    }
//
//    @FXML
//    private void onClose(ActionEvent event) {
//        // Close the window
//        Stage stage = (Stage) btnClose.getScene().getWindow();
//        stage.close();
//    }
//}

package HotelApp;

import HotelApp.model.Checkin;
import HotelApp.repository.CheckinRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.sql.*;
import HotelApp.db.DButil;
import javafx.collections.ObservableList;

public class CheckInFormController {

    @FXML private BorderPane rootPane;
    @FXML private Label lblTitle;
    @FXML private TextField txtBooker;
    @FXML private TextField txtPhone;
    @FXML private ComboBox<String> cbPayment;
    @FXML private TextField txtDeposit;
    @FXML private Button btnAddRoom;
    @FXML private Button btnRemoveRoom;
    @FXML private TableView<RoomVM> tblRooms;
    @FXML private TableColumn<RoomVM, String> colRoomNum;
    @FXML private TableColumn<RoomVM, String> colCategory;
    @FXML private TableColumn<RoomVM, String> colQuality;
    @FXML private TableColumn<RoomVM, Number> colPrice;
    @FXML private TableColumn<RoomVM, String> colCustomer;
    @FXML private TableColumn<RoomVM, String> colServices;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnClose;

    private String stayingId;

    // Updated record for room view model with customer and services
    public record RoomVM(String roomNumber, String category, String quality, double price, 
                         String customer, String services) {}

    @FXML
    private void initialize() {
        // Set up ComboBox with payment options
        cbPayment.setItems(FXCollections.observableArrayList("Cash", "Credit Card", "Debit Card"));

        // Configure table columns
        colRoomNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().roomNumber()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category()));
        colQuality.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().quality()));
        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().price()));
        colCustomer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().customer()));
        colServices.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().services()));

        // Initialize table with empty data
        tblRooms.setItems(FXCollections.observableArrayList());
    }

    public void setBookingData(Checkin booking, String stayingId) {
        this.stayingId = stayingId;

        // Populate form fields
        txtBooker.setText(booking.getGuestName());
        txtPhone.setText(booking.getGuestPhone());
        cbPayment.setValue(getPaymentMethod(booking.getGuestPhone()));
        txtDeposit.setText(getDepositAmount(booking.getGuestPhone()));

        // Populate table with room data
        populateRoomTable(booking);
    }

    private String getPaymentMethod(String phone) {
        String sql = """
            SELECT Payment_method
            FROM Booking_Management
            WHERE Book_contact = ?
        """;
        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Payment_method");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getDepositAmount(String phone) {
        String sql = """
            SELECT Deposit_amount
            FROM Booking_Management
            WHERE Book_contact = ?
        """;
        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return String.valueOf(rs.getInt("Deposit_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void populateRoomTable(Checkin booking) {
        ObservableList<RoomVM> rooms = FXCollections.observableArrayList();
        String sql = """
            SELECT 
                rm.Room_num,
                rm.Room_category,
                rm.Room_quality,
                rm.Room_price,
                cm.Customer_name,
                STRING_AGG(sm.Service_name, ', ') AS Services
            FROM Booking_Management bm
            JOIN Booking_Room br ON bm.Booking_id = br.Booking_id
            JOIN Room_Management rm ON br.Room_id = rm.Room_id
            LEFT JOIN Staying_Room_Customer src ON rm.Room_id = src.Room_id
            LEFT JOIN Customer_Management cm ON src.Customer_id = cm.Customer_id
            LEFT JOIN Staying_Room_Service srs ON rm.Room_id = srs.Room_id AND srs.Staying_id = ?
            LEFT JOIN Service_Management sm ON srs.Service_id = sm.Service_id
            WHERE bm.Book_contact = ?
            GROUP BY rm.Room_num, rm.Room_category, rm.Room_quality, rm.Room_price, cm.Customer_name
        """;

        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stayingId);
            pstmt.setString(2, booking.getGuestPhone());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(new RoomVM(
                    rs.getString("Room_num"),
                    rs.getString("Room_category"),
                    rs.getString("Room_quality"),
                    rs.getDouble("Room_price"),
                    rs.getString("Customer_name") != null ? rs.getString("Customer_name") : "",
                    rs.getString("Services") != null ? rs.getString("Services") : ""
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tblRooms.setItems(rooms);
    }

    @FXML
    private void onAddRoom(ActionEvent event) {
        // TODO: Implement logic to add a room
        System.out.println("Add Room clicked");
    }

    @FXML
    private void onRemoveRoom(ActionEvent event) {
        // TODO: Implement logic to remove selected room
        System.out.println("Remove Room clicked");
    }

    @FXML
    private void onDelete(ActionEvent event) {
        // TODO: Implement logic to delete booking
        System.out.println("Delete clicked");
    }

    @FXML
    private void onSave(ActionEvent event) {
        // TODO: Implement logic to save booking
        System.out.println("Save clicked");
    }

    @FXML
    private void onClose(ActionEvent event) {
        // Close the window
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}