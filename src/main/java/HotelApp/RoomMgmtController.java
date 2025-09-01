package HotelApp;

import HotelApp.model.Room;
import HotelApp.repository.RoomRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    @FXML
    private TextField txtAmenities;
    @FXML
    private TextField txtCapacity;

    private final ObservableList<Room> rooms = FXCollections.observableArrayList();
    private Room selectedRoom;

    @FXML
    private void initialize() {
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
        cbCategory.setItems(FXCollections.observableArrayList("single", "double", "suite"));

        // Setup qualities
        cbQuality.setItems(FXCollections.observableArrayList(
                "standard", "superior", "deluxe", "premium"
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
        txtAmenities.setText(room.getAmenities());
        txtCapacity.setText(String.valueOf(room.getCapacity()));
    }

    @FXML
    private void onAddRoom() {
        try {
            validateInput();
            Room newRoom = new Room(
                    RoomRepository.generateNextRoomId(),
                    txtRoomNumber.getText(),
                    cbCategory.getValue(),
                    cbQuality.getValue(),
                    Integer.parseInt(txtPrice.getText()),
                    cbStatus.getValue(),
                    txtAmenities.getText(),
                    Integer.parseInt(txtCapacity.getText())
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
            Room updatedRoom = new Room(
                    selectedRoom.getRoomId(),
                    txtRoomNumber.getText(),
                    cbCategory.getValue(),
                    cbQuality.getValue(),
                    Integer.parseInt(txtPrice.getText()),
                    cbStatus.getValue(),
                    txtAmenities.getText(),
                    Integer.parseInt(txtCapacity.getText())
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
        txtAmenities.clear();
        txtCapacity.clear();
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
        if (txtCapacity.getText().trim().isEmpty()) {
            throw new Exception("Capacity is required");
        }
        try {
            Integer.parseInt(txtCapacity.getText());
        } catch (NumberFormatException e) {
            throw new Exception("Capacity must be a number");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
