package HotelApp;

import java.io.IOException;

import HotelApp.model.Room;
import HotelApp.repository.RoomRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class RoomMgmtController {

    @FXML
    private TableView<Room> tblRooms;
    @FXML
    private TableColumn<Room, String> colRoomId;
    @FXML
    private TableColumn<Room, String> colRoomNum;
    @FXML
    private TableColumn<Room, String> colCategory;
    @FXML
    private TableColumn<Room, String> colQuality;
    @FXML
    private TableColumn<Room, Number> colPrice;
    @FXML
    private TableColumn<Room, String> colStatus;
    @FXML
    private TableColumn<Room, String> colAmenities;
    @FXML
    private TableColumn<Room, Number> colCapacity;

    @FXML
    private TextField txtRoomNumber;
    @FXML
    private ChoiceBox<String> cbCategory;
    @FXML
    private ChoiceBox<String> cbQuality;
    @FXML
    private TextField txtPrice;
    @FXML
    private ChoiceBox<String> cbStatus;

    private final ObservableList<Room> rooms = FXCollections.observableArrayList();
    private Room selectedRoom;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupChoiceBoxes();
        loadRooms();
        setupTableSelection();
    }

    private void setupTableColumns() {
        colRoomId.setCellValueFactory(data -> data.getValue().roomIdProperty());
        colRoomNum.setCellValueFactory(data -> data.getValue().roomNumberProperty());
        colCategory.setCellValueFactory(data -> data.getValue().categoryProperty());
        colQuality.setCellValueFactory(data -> data.getValue().qualityProperty());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        colAmenities.setCellValueFactory(data -> data.getValue().amenitiesProperty());
        colCapacity.setCellValueFactory(data -> data.getValue().capacityProperty());
    }

    private void setupChoiceBoxes() {
        // Setup categories
        cbCategory.setItems(FXCollections.observableArrayList("Single", "Double", "Suite"));

        // Setup qualities
    cbQuality.setItems(FXCollections.observableArrayList(
        "Standard", "Superior", "Deluxe", "Premium"
    ));

        // Setup statuses
    cbStatus.setItems(FXCollections.observableArrayList(
        "Available", "Occupied", "Cleaning"
    ));
    }

    private void loadRooms() {
        rooms.clear();
        rooms.addAll(RoomRepository.getAll());
        tblRooms.setItems(rooms);
    }

    private void setupTableSelection() {
        tblRooms.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedRoom = newSelection;
                showRoomDetails(selectedRoom);
            }
        });
    }

    private void showRoomDetails(Room room) {
        txtRoomNumber.setText(room.getRoomNumber());
        cbCategory.setValue(room.getCategory());
        cbQuality.setValue(room.getQuality());
        txtPrice.setText(String.valueOf(room.getPrice()));
        cbStatus.setValue(room.getStatus());
    }

    @FXML
    private void onAddRoom() {
        try {
            validateInput();
            // Lấy amenities và capacity từ RoomType_Amenity
            var roomTypeInfo = RoomRepository.getRoomTypeInfo(
                    cbCategory.getValue(),
                    cbQuality.getValue()
            );
            if (roomTypeInfo == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Room type configuration not found");
                return;
            }

            Room newRoom = new Room(
                    RoomRepository.generateNextRoomId(),
                    txtRoomNumber.getText(),
                    cbCategory.getValue(),
                    cbQuality.getValue(),
                    Integer.parseInt(txtPrice.getText()),
                    cbStatus.getValue(),
                    roomTypeInfo.amenities(),
                    roomTypeInfo.capacity()
            );

            if (RoomRepository.addRoom(newRoom)) {
                loadRooms();
                onClear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add room");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onUpdateRoom() {
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a room to update");
            return;
        }

        try {
            validateInput();
            // Lấy amenities và capacity từ RoomType_Amenity
            var roomTypeInfo = RoomRepository.getRoomTypeInfo(
                    cbCategory.getValue(),
                    cbQuality.getValue()
            );
            if (roomTypeInfo == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Room type configuration not found");
                return;
            }

            Room updatedRoom = new Room(
                    selectedRoom.getRoomId(),
                    txtRoomNumber.getText(),
                    cbCategory.getValue(),
                    cbQuality.getValue(),
                    Integer.parseInt(txtPrice.getText()),
                    cbStatus.getValue(),
                    roomTypeInfo.amenities(),
                    roomTypeInfo.capacity()
            );

            if (RoomRepository.updateRoom(updatedRoom)) {
                loadRooms();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update room");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onDeleteRoom() {
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a room to delete");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Room");
        confirmDialog.setContentText("Are you sure you want to delete this room?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            if (RoomRepository.deleteRoom(selectedRoom.getRoomId())) {
                loadRooms();
                onClear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete room");
            }
        }
    }

    @FXML
    private void onClear() {
        txtRoomNumber.clear();
        cbCategory.setValue(null);
        cbQuality.setValue(null);
        txtPrice.clear();
        cbStatus.setValue(null);
        selectedRoom = null;
        tblRooms.getSelectionModel().clearSelection();
    }

    private void validateInput() throws Exception {
        if (txtRoomNumber.getText().trim().isEmpty()) {
            throw new Exception("Room number is required");
        }
        if (cbCategory.getValue() == null) {
            throw new Exception("Category is required");
        }
        if (cbQuality.getValue() == null) {
            throw new Exception("Quality is required");
        }
        if (txtPrice.getText().trim().isEmpty()) {
            throw new Exception("Price is required");
        }
        try {
            Integer.parseInt(txtPrice.getText());
        } catch (NumberFormatException e) {
            throw new Exception("Price must be a number");
        }
        if (cbStatus.getValue() == null) {
            throw new Exception("Status is required");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onManageAmenities() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RoomTypeAmenities.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Room Types & Amenities Management");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh room list after managing amenities
            loadRooms();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Room Types & Amenities window");
        }
    }
}
