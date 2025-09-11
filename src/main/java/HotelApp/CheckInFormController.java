package HotelApp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

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
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnClose;

    // Simple record for room view model
    public record RoomVM(String roomNumber, String category, String quality, double price) {}

    @FXML
    private void initialize() {
        // Set up ComboBox with payment options
        cbPayment.setItems(FXCollections.observableArrayList("Cash", "Credit Card", "Debit Card"));

        // Configure table columns
        colRoomNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().roomNumber()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category()));
        colQuality.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().quality()));
        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().price()));

        // Initialize table with empty data
        tblRooms.setItems(FXCollections.observableArrayList());
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
