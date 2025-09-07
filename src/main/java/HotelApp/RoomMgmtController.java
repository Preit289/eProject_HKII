package HotelApp;

import HotelApp.repository.RoomRepository;
import HotelApp.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RoomMgmtController {

    @FXML
    private TextField txtRoomNumber;
    @FXML
    private ChoiceBox<String> cbType;
    @FXML
    private ChoiceBox<String> cbStatus;

    @FXML
    private TableView<Room> tblRooms;
    @FXML
    private TableColumn<Room, String> colRoom;
    @FXML
    private TableColumn<Room, String> colType;
    @FXML
    private TableColumn<Room, String> colStatus;

    private final ObservableList<Room> rooms = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colRoom.setCellValueFactory(data -> data.getValue().roomNumberProperty());
        colType.setCellValueFactory(data -> data.getValue().typeProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        // Load rooms from repository
        rooms.addAll(RoomRepository.getAll());
        tblRooms.setItems(rooms);
    }

    @FXML
    private void onAddRoom() {
        String roomNum = txtRoomNumber.getText();
        String type = cbType.getValue();
        String status = cbStatus.getValue();

        if (roomNum.isBlank() || type == null || status == null) {
            showAlert("Please fill in all fields.");
            return;
        }

        Room room = new Room(roomNum, type, status);
        rooms.add(room);
        RoomRepository.save(room); // Save to repository
        clearForm();
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private void clearForm() {
        txtRoomNumber.clear();
        cbType.setValue(null);
        cbStatus.setValue(null);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
