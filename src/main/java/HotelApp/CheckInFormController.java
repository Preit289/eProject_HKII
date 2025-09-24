package HotelApp;

import HotelApp.model.Checkin;
import HotelApp.repository.CheckinRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.sql.*;
import HotelApp.db.DButil;
import java.util.Map;

import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

import javafx.scene.text.Text;
import javafx.print.PrinterJob;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;

public class CheckInFormController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtBooker;
    @FXML
    private TextField txtPhone;
    @FXML
    private ComboBox<String> cbPayment;
    @FXML
    private TextField txtDeposit;
    @FXML
    private TextField txtCheckinDate;
    @FXML
    private TextField txtCheckoutDate;
    @FXML
    private Button btnAddRoom;
    @FXML
    private Button btnRemoveRoom;
    @FXML
    private TableView<RoomVM> tblRooms;
    @FXML
    private TableColumn<RoomVM, String> colRoomNum;
    @FXML
    private TableColumn<RoomVM, String> colCategory;
    @FXML
    private TableColumn<RoomVM, String> colQuality;
    @FXML
    private TableColumn<RoomVM, Number> colPrice;
    @FXML
    private TableColumn<RoomVM, String> colCustomer;
    @FXML
    private TableColumn<RoomVM, String> colServices;
    @FXML
    private TableColumn<RoomVM, Void> colAssign;
    @FXML
    private TableColumn<RoomVM, Void> colAssignService;
    @FXML
    private TableColumn<RoomVM, Void> colRemoveCustomer;
    @FXML
    private TableColumn<RoomVM, Void> colRemoveService;
    @FXML
    private TableColumn<RoomVM, Void> colEditServiceQty;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnPrint;

    private String stayingId;
    private Checkin currentBooking;

    // Record for room view model with customer and services
    public record RoomVM(String roomNumber, String category, String quality, double price,
            String customer, String services) {
    }

    @FXML
    private void initialize() {
        // Set up ComboBox with payment options
        cbPayment.setItems(FXCollections.observableArrayList("Cash", "Credit Card", "Debit Card"));

        // Configure table columns
        colRoomNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().roomNumber()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category()));
        colQuality.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().quality()));
        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().price()));

        // Custom CellFactory for multi-line Customer column
        colCustomer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().customer()));
        colCustomer.setCellFactory(column -> new TableCell<RoomVM, String>() {
            private final Text text = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.setWrappingWidth(colCustomer.getWidth() - 10);
                    setGraphic(text);
                    setPrefHeight(text.getBoundsInLocal().getHeight() + 10);
                }
            }
        });

        // Custom CellFactory for multi-line Services column
        colServices.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().services()));
        colServices.setCellFactory(column -> new TableCell<RoomVM, String>() {
            private final Text text = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.setWrappingWidth(colServices.getWidth() - 10);
                    setGraphic(text);
                    setPrefHeight(text.getBoundsInLocal().getHeight() + 10);
                }
            }
        });

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

        // Set up Remove Customer button column
        colRemoveCustomer.setCellFactory(param -> new TableCell<RoomVM, Void>() {
            private final Button removeButton = new Button("Remove Customer");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    removeButton.setOnAction(event -> {
                        RoomVM room = getTableView().getItems().get(getIndex());
                        onRemoveCustomer(room);
                    });
                    setGraphic(removeButton);
                    setText(null);
                }
            }
        });

        // Set up Remove Service button column
        colRemoveService.setCellFactory(param -> new TableCell<RoomVM, Void>() {
            private final Button removeServiceButton = new Button("Remove Service");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    removeServiceButton.setOnAction(event -> {
                        RoomVM room = getTableView().getItems().get(getIndex());
                        onRemoveService(room);
                    });
                    setGraphic(removeServiceButton);
                    setText(null);
                }
            }
        });

        // Set up Edit Service Quantity button column
        colEditServiceQty.setCellFactory(param -> new TableCell<RoomVM, Void>() {
            private final Button editQtyButton = new Button("Edit Qty");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    editQtyButton.setOnAction(event -> {
                        RoomVM room = getTableView().getItems().get(getIndex());
                        onEditServiceQuantity(room);
                    });
                    setGraphic(editQtyButton);
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

        txtBooker.setText(booking.getGuestName());
        txtPhone.setText(booking.getGuestPhone());
        cbPayment.setValue(getPaymentMethod(booking.getGuestPhone()));
        txtDeposit.setText(getDepositAmount(booking.getGuestPhone()));
        txtCheckinDate.setText(getCheckinDate(stayingId));
        txtCheckoutDate.setText(getCheckoutDate(stayingId));

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

    private String getCheckinDate(String stayingId) {
        String sql = """
                    SELECT Checkin_date
                    FROM Staying_Management
                    WHERE Staying_id = ?
                """;
        try (Connection conn = DButil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stayingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getTimestamp("Checkin_date") != null) {
                return rs.getTimestamp("Checkin_date").toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCheckoutDate(String stayingId) {
        String sql = """
                    SELECT Checkout_date
                    FROM Staying_Management
                    WHERE Staying_id = ?
                """;
        try (Connection conn = DButil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stayingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getTimestamp("Checkout_date") != null) {
                return rs.getTimestamp("Checkout_date").toString();
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
                        COALESCE(cust_agg.Customers, '') AS Customer_names,
                        COALESCE(serv_agg.Services, '') AS Services
                    FROM Booking_Management bm
                    JOIN Booking_Room br ON bm.Booking_id = br.Booking_id
                    JOIN Room_Management rm ON br.Room_id = rm.Room_id
                    LEFT JOIN (
                        SELECT src.Room_id, STRING_AGG(cm.Customer_name, '\n') AS Customers
                        FROM Staying_Room_Customer src
                        JOIN Customer_Management cm ON src.Customer_id = cm.Customer_id
                        WHERE src.Staying_id = ?
                        GROUP BY src.Room_id
                    ) cust_agg ON rm.Room_id = cust_agg.Room_id
                    LEFT JOIN (
                        SELECT srs.Room_id, STRING_AGG(sm.Service_name + ' x ' + CAST(srs.Quantity AS NVARCHAR), '\n') AS Services
                        FROM Staying_Room_Service srs
                        JOIN Service_Management sm ON srs.Service_id = sm.Service_id
                        WHERE srs.Staying_id = ?
                        GROUP BY srs.Room_id
                    ) serv_agg ON rm.Room_id = serv_agg.Room_id
                    WHERE bm.Book_contact = ?
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
                        rs.getString("Customer_names"),
                        rs.getString("Services")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tblRooms.setItems(rooms);
    }

    private void onAssignCustomer(RoomVM room) {
        Dialog<ObservableList<String>> dialog = new Dialog<>();
        dialog.setTitle("Assign Customers to Room " + room.roomNumber());
        dialog.setHeaderText("Select customers to assign to room " + room.roomNumber());

        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        ListView<String> customerListView = new ListView<>();
        customerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        customerListView.setPrefHeight(200);
        Map<String, String> customerMap = CheckinRepository.getAllCustomers();
        customerListView.setItems(FXCollections.observableArrayList(customerMap.keySet()));

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Customers:"), customerListView);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().lookupButton(assignButtonType).setDisable(true);
        customerListView.getSelectionModel().getSelectedItems()
                .addListener((javafx.beans.Observable observable) -> dialog.getDialogPane()
                        .lookupButton(assignButtonType).setDisable(
                                customerListView.getSelectionModel().getSelectedItems().isEmpty()));

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
                        CheckinRepository.assignCustomerToRoom(stayingId, room.roomNumber(), customerId);
                    } else {
                        success = false;
                    }
                }
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

    private void onRemoveCustomer(RoomVM room) {
        Dialog<ObservableList<String>> dialog = new Dialog<>();
        dialog.setTitle("Remove Customers from Room " + room.roomNumber());
        dialog.setHeaderText("Select customers to remove from room " + room.roomNumber());

        ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);

        ListView<String> customerListView = new ListView<>();
        customerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        customerListView.setPrefHeight(200);

        // Fetch customers assigned to this room
        Map<String, String> customerMap = CheckinRepository.getCustomersForRoom(stayingId, room.roomNumber());
        customerListView.setItems(FXCollections.observableArrayList(customerMap.keySet()));

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Assigned Customers:"), customerListView);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().lookupButton(removeButtonType).setDisable(true);
        customerListView.getSelectionModel().getSelectedItems()
                .addListener((javafx.beans.Observable observable) -> dialog.getDialogPane()
                        .lookupButton(removeButtonType).setDisable(
                                customerListView.getSelectionModel().getSelectedItems().isEmpty()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == removeButtonType) {
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
                        CheckinRepository.removeCustomerFromRoom(stayingId, room.roomNumber(), customerId);
                    } else {
                        success = false;
                    }
                }
                populateRoomTable(currentBooking);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customers removed successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to remove one or more customers.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to remove customers: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void onAssignService(RoomVM room) {
        Dialog<ObservableList<String>> dialog = new Dialog<>();
        dialog.setTitle("Assign Services to Room " + room.roomNumber());
        dialog.setHeaderText("Select services to assign to room " + room.roomNumber());

        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        ListView<String> serviceListView = new ListView<>();
        serviceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        serviceListView.setPrefHeight(200);
        Map<String, String> serviceMap = CheckinRepository.getAllServices();
        serviceListView.setItems(FXCollections.observableArrayList(serviceMap.keySet()));

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Services:"), serviceListView);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().lookupButton(assignButtonType).setDisable(true);
        serviceListView.getSelectionModel().getSelectedItems()
                .addListener((javafx.beans.Observable observable) -> dialog.getDialogPane()
                        .lookupButton(assignButtonType).setDisable(
                                serviceListView.getSelectionModel().getSelectedItems().isEmpty()));

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
                        CheckinRepository.assignServiceToRoom(stayingId, room.roomNumber(), serviceId);
                    } else {
                        success = false;
                    }
                }
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

    private void onRemoveService(RoomVM room) {
        Dialog<ObservableList<String>> dialog = new Dialog<>();
        dialog.setTitle("Remove Services from Room " + room.roomNumber());
        dialog.setHeaderText("Select services to remove from room " + room.roomNumber());

        ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);

        ListView<String> serviceListView = new ListView<>();
        serviceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        serviceListView.setPrefHeight(200);

        Map<String, String> serviceMap = CheckinRepository.getServicesForRoom(stayingId, room.roomNumber());
        serviceListView.setItems(FXCollections.observableArrayList(serviceMap.keySet()));

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Assigned Services:"), serviceListView);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().lookupButton(removeButtonType).setDisable(true);
        serviceListView.getSelectionModel().getSelectedItems()
                .addListener((javafx.beans.Observable observable) -> dialog.getDialogPane()
                        .lookupButton(removeButtonType).setDisable(
                                serviceListView.getSelectionModel().getSelectedItems().isEmpty()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == removeButtonType) {
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
                        CheckinRepository.removeServiceFromRoom(stayingId, room.roomNumber(), serviceId);
                    } else {
                        success = false;
                    }
                }
                populateRoomTable(currentBooking);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Services removed successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to remove one or more services.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to remove services: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void onEditServiceQuantity(RoomVM room) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Service Quantity for Room " + room.roomNumber());
        dialog.setHeaderText("Select a service and update its quantity for room " + room.roomNumber());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        ComboBox<String> serviceComboBox = new ComboBox<>();
        TextField txtQuantity = new TextField();
        txtQuantity.setPromptText("Enter quantity (1 or more)");
        Map<String, String> serviceMap = CheckinRepository.getServicesForRoom(stayingId, room.roomNumber());
        serviceComboBox.setItems(FXCollections.observableArrayList(serviceMap.keySet()));

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Select Service:"), serviceComboBox,
                new Label("Quantity:"), txtQuantity);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().lookupButton(updateButtonType).setDisable(true);
        serviceComboBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> dialog.getDialogPane().lookupButton(updateButtonType).setDisable(
                        newVal == null || txtQuantity.getText().trim().isEmpty()));
        txtQuantity.textProperty()
                .addListener((obs, oldVal, newVal) -> dialog.getDialogPane().lookupButton(updateButtonType).setDisable(
                        serviceComboBox.getSelectionModel().isEmpty() || newVal.trim().isEmpty()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String serviceName = serviceComboBox.getValue();
                String quantityText = txtQuantity.getText().trim();
                try {
                    int quantity = Integer.parseInt(quantityText);
                    if (quantity < 1) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Quantity must be at least 1.");
                        alert.showAndWait();
                        return null;
                    }
                    String serviceId = serviceMap.get(serviceName);
                    if (serviceId != null) {
                        CheckinRepository.updateServiceQuantity(stayingId, room.roomNumber(), serviceId, quantity);
                        populateRoomTable(currentBooking);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service quantity updated successfully.");
                        alert.showAndWait();
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid quantity format.");
                    alert.showAndWait();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Failed to update service quantity: " + e.getMessage());
                    alert.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void onAddRoom(ActionEvent event) {
        System.out.println("Add Room clicked");
    }

    @FXML
    private void onRemoveRoom(ActionEvent event) {
        System.out.println("Remove Room clicked");
    }

    @FXML
    private void onDelete(ActionEvent event) {
        System.out.println("Delete clicked");
    }

    @FXML
    private void onUpdate(ActionEvent event) {
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
            CheckinRepository.updateBookingAndStay(
                    currentBooking.getGuestPhone(),
                    currentBooking.getGuestName(),
                    stayingId,
                    paymentMethod,
                    depositAmount);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Booking and stay updated successfully.");
            alert.showAndWait();

            populateRoomTable(currentBooking);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save changes: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onPrint() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null || !job.showPrintDialog(btnPrint.getScene().getWindow()))
            return;

        PageLayout pageLayout = job.getJobSettings().getPageLayout();
        double pageWidth = pageLayout.getPrintableWidth();
        double pageHeight = pageLayout.getPrintableHeight();

        // ===== Root content =====
        VBox printContent = new VBox(15);
        printContent.setPadding(new Insets(30));
        printContent.setStyle("-fx-background-color: white;");
        printContent.setPrefWidth(pageWidth * 0.8); // nhỏ hơn A4 một chút để có lề
        printContent.setMaxWidth(pageWidth * 0.8);

        // ===== Header =====
        Label hotelName = new Label("HOTEL");
        hotelName.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        hotelName.setAlignment(Pos.CENTER);
        hotelName.setMaxWidth(Double.MAX_VALUE);

        Label billTitle = new Label("INVOICE");
        billTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 10 0 20 0;");
        billTitle.setAlignment(Pos.CENTER);
        billTitle.setMaxWidth(Double.MAX_VALUE);

        // ===== Guest Info =====
        GridPane guestInfo = new GridPane();
        guestInfo.setHgap(15);
        guestInfo.setVgap(8);
        guestInfo.addRow(0, new Label("Guest Name:"), new Label(txtBooker.getText()));
        guestInfo.addRow(1, new Label("Phone:"), new Label(txtPhone.getText()));
        guestInfo.addRow(2, new Label("Check-in:"), new Label(txtCheckinDate.getText()));
        guestInfo.addRow(3, new Label("Check-out:"), new Label(txtCheckoutDate.getText()));
        guestInfo.getChildren().filtered(n -> n instanceof Label)
                .forEach(n -> ((Label) n).setStyle("-fx-font-size: 12;"));

        // ===== Room list (GridPane thay cho TableView) =====
        GridPane roomGrid = new GridPane();
        roomGrid.setHgap(15);
        roomGrid.setVgap(5);
        roomGrid.addRow(0,
                new Label("Room No."), new Label("Category"),
                new Label("Price"), new Label("Services"));

        int row = 1;
        for (RoomVM room : tblRooms.getItems()) {
            roomGrid.addRow(row++,
                    new Label(room.roomNumber()),
                    new Label(room.category()),
                    new Label(String.format("$%.2f", room.price())),
                    new Label(room.services()));
        }

        // ===== Summary =====
        double roomTotal = tblRooms.getItems().stream().mapToDouble(RoomVM::price).sum();
        double deposit = txtDeposit.getText().isEmpty() ? 0 : Double.parseDouble(txtDeposit.getText());
        double balance = roomTotal - deposit;

        GridPane summary = new GridPane();
        summary.setHgap(50);
        summary.setVgap(8);
        summary.addRow(0, new Label("Payment Method:"), new Label(cbPayment.getValue()));
        summary.addRow(1, new Label("Total Amount:"), new Label(String.format("$%.2f", roomTotal)));
        summary.addRow(2, new Label("Deposit Paid:"), new Label(String.format("$%.2f", deposit)));
        summary.addRow(3, new Label("Balance Due:"), new Label(String.format("$%.2f", balance)));
        summary.getChildren().filtered(n -> n instanceof Label)
                .forEach(n -> ((Label) n).setStyle("-fx-font-size: 14; -fx-font-weight: bold;"));

        // ===== Footer =====
        Label footer = new Label("Thank you for choosing our hotel!");
        footer.setStyle("-fx-font-size: 12; -fx-font-style: italic; -fx-padding: 20 0 0 0;");
        footer.setAlignment(Pos.CENTER);
        footer.setMaxWidth(Double.MAX_VALUE);

        // Gắn tất cả vào nội dung
        printContent.getChildren().addAll(
                hotelName, billTitle, guestInfo,
                new Separator(), new Label("Room Details:"), roomGrid,
                new Separator(), summary, footer);

        // ===== Wrapper để căn giữa trên A4 =====
        StackPane wrapper = new StackPane(printContent);
        wrapper.setPrefSize(pageWidth, pageHeight);
        StackPane.setAlignment(printContent, Pos.TOP_CENTER);

        // ===== Page break =====
        double totalHeight = printContent.prefHeight(-1);
        double y = 0;
        while (y < totalHeight) {
            Group page = new Group(wrapper);
            page.setClip(new Rectangle(0, y, pageWidth, pageHeight));
            page.setTranslateY(-y);

            if (!job.printPage(page))
                break;
            y += pageHeight;
        }

        job.endJob();
        new Alert(Alert.AlertType.INFORMATION, "Bill printed successfully!").showAndWait();
    }

}