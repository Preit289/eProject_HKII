package HotelApp;

import HotelApp.model.Checkin;
import HotelApp.repository.CheckinRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.sql.*;
import HotelApp.db.DButil;
import javafx.scene.layout.VBox;
import javafx.scene.control.SelectionMode;
import java.util.Map;

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
    @FXML private TableColumn<RoomVM, Void> colAssign;
    @FXML private TableColumn<RoomVM, Void> colAssignService;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnClose;

    private String stayingId;
    private Checkin currentBooking;

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

        // Set up Assign Customer button column
        colAssign.setCellFactory(param -> new TableCell<RoomVM, Void>() {
            private final Button assignButton = new Button("Assign Customer");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    assignButton.setOnAction(event -> {
                        RoomVM room = getTableView().getItems().get(getIndex());
                        onAssignCustomer(room);
                    });
                    setGraphic(assignButton);
                    setText(null);
                }
            }
        });

        // Set up Assign Service button column
        colAssignService.setCellFactory(param -> new TableCell<RoomVM, Void>() {
            private final Button assignServiceButton = new Button("Assign Service");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    assignServiceButton.setOnAction(event -> {
                        RoomVM room = getTableView().getItems().get(getIndex());
                        onAssignService(room);
                    });
                    setGraphic(assignServiceButton);
                    setText(null);
                }
            }
        });

        // Initialize table with empty data
        tblRooms.setItems(FXCollections.observableArrayList());
    }

    public void setBookingData(Checkin booking, String stayingId) {
        this.stayingId = stayingId;
        this.currentBooking = booking;

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
                STRING_AGG(cm.Customer_name, ', ') AS Customer_names,
                STRING_AGG(sm.Service_name, ', ') AS Services
            FROM Booking_Management bm
            JOIN Booking_Room br ON bm.Booking_id = br.Booking_id
            JOIN Room_Management rm ON br.Room_id = rm.Room_id
            LEFT JOIN Staying_Room_Customer src ON rm.Room_id = src.Room_id AND src.Staying_id = ?
            LEFT JOIN Customer_Management cm ON src.Customer_id = cm.Customer_id
            LEFT JOIN Staying_Room_Service srs ON rm.Room_id = srs.Room_id AND srs.Staying_id = ?
            LEFT JOIN Service_Management sm ON srs.Service_id = sm.Service_id
            WHERE bm.Book_contact = ?
            GROUP BY rm.Room_num, rm.Room_category, rm.Room_quality, rm.Room_price
        """;

        try (Connection conn = DButil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stayingId);
            pstmt.setString(2, stayingId);
            pstmt.setString(3, booking.getGuestPhone());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(new RoomVM(
                    rs.getString("Room_num"),
                    rs.getString("Room_category"),
                    rs.getString("Room_quality"),
                    rs.getDouble("Room_price"),
                    rs.getString("Customer_names") != null ? rs.getString("Customer_names") : "",
                    rs.getString("Services") != null ? rs.getString("Services") : ""
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tblRooms.setItems(rooms);
    }

    private void onAssignCustomer(RoomVM room) {
        // Create a dialog to select customers
        Dialog<ObservableList<String>> dialog = new Dialog<>();
        dialog.setTitle("Assign Customers to Room " + room.roomNumber());
        dialog.setHeaderText("Select customers to assign to room " + room.roomNumber());

        // Set the button types
        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        // Create ListView for customer selection
        ListView<String> customerListView = new ListView<>();
        customerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        customerListView.setPrefHeight(200);
        Map<String, String> customerMap = CheckinRepository.getAllCustomers();
        customerListView.setItems(FXCollections.observableArrayList(customerMap.keySet()));

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Customers:"), customerListView);
        dialog.getDialogPane().setContent(content);

        // Enable the Assign button only if at least one customer is selected
        dialog.getDialogPane().lookupButton(assignButtonType).setDisable(true);
        customerListView.getSelectionModel().getSelectedItems().addListener((javafx.beans.Observable observable) ->
            dialog.getDialogPane().lookupButton(assignButtonType).setDisable(
                customerListView.getSelectionModel().getSelectedItems().isEmpty()
            )
        );

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButtonType) {
                return customerListView.getSelectionModel().getSelectedItems();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedCustomers -> {
            try {
                boolean success = true;
                for (String customerName : selectedCustomers) {
                    String customerId = customerMap.get(customerName);
                    if (customerId != null) {
                        // Assign customer to room
                        CheckinRepository.assignCustomerToRoom(stayingId, room.roomNumber(), customerId);
                    } else {
                        success = false;
                    }
                }
                // Refresh the table to show updated customers
                populateRoomTable(currentBooking);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customers assigned successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to assign one or more customers.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to assign customers: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void onAssignService(RoomVM room) {
        // Create a dialog to select services
        Dialog<ObservableList<String>> dialog = new Dialog<>();
        dialog.setTitle("Assign Services to Room " + room.roomNumber());
        dialog.setHeaderText("Select services to assign to room " + room.roomNumber());

        // Set the button types
        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        // Create ListView for service selection
        ListView<String> serviceListView = new ListView<>();
        serviceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        serviceListView.setPrefHeight(200);
        Map<String, String> serviceMap = CheckinRepository.getAllServices();
        serviceListView.setItems(FXCollections.observableArrayList(serviceMap.keySet()));

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Services:"), serviceListView);
        dialog.getDialogPane().setContent(content);

        // Enable the Assign button only if at least one service is selected
        dialog.getDialogPane().lookupButton(assignButtonType).setDisable(true);
        serviceListView.getSelectionModel().getSelectedItems().addListener((javafx.beans.Observable observable) ->
            dialog.getDialogPane().lookupButton(assignButtonType).setDisable(
                serviceListView.getSelectionModel().getSelectedItems().isEmpty()
            )
        );

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButtonType) {
                return serviceListView.getSelectionModel().getSelectedItems();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedServices -> {
            try {
                boolean success = true;
                for (String serviceName : selectedServices) {
                    String serviceId = serviceMap.get(serviceName);
                    if (serviceId != null) {
                        // Assign service to room
                        CheckinRepository.assignServiceToRoom(stayingId, room.roomNumber(), serviceId);
                    } else {
                        success = false;
                    }
                }
                // Refresh the table to show updated services
                populateRoomTable(currentBooking);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Services assigned successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to assign one or more services.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to assign services: " + e.getMessage());
                alert.showAndWait();
            }
        });
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
        // Validate inputs
        String paymentMethod = cbPayment.getValue();
        String depositText = txtDeposit.getText();
        String booker = txtBooker.getText();
        String phone = txtPhone.getText();

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Payment method is required.");
            alert.showAndWait();
            return;
        }

        int depositAmount;
        try {
            depositAmount = depositText.isEmpty() ? 0 : Integer.parseInt(depositText);
            if (depositAmount < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Deposit amount cannot be negative.");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid deposit amount format.");
            alert.showAndWait();
            return;
        }

        if (booker.trim().isEmpty() || phone.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Booker name and phone are required.");
            alert.showAndWait();
            return;
        }

        try {
            // Update Booking_Management and Staying_Management
            CheckinRepository.updateBookingAndStay(
                currentBooking.getGuestPhone(),
                currentBooking.getGuestName(),
                stayingId,
                paymentMethod,
                depositAmount
            );

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Booking and stay updated successfully.");
            alert.showAndWait();

            // Refresh the table
            populateRoomTable(currentBooking);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save changes: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onClose(ActionEvent event) {
        // Close the window
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}