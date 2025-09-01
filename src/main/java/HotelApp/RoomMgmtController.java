package HotelApp;

import HotelApp.model.Room;
import HotelApp.repository.RoomRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;

public class RoomMgmtController {

    @FXML private TextField txtRoomNumber;
    @FXML private ChoiceBox<String> cbType;    // Single / Double / Suite
    @FXML private ChoiceBox<String> cbStatus;  // Empty / Occupied / Cleaning

    @FXML private TableView<Room> tblRooms;
    @FXML private TableColumn<Room, String> colRoom;
    @FXML private TableColumn<Room, String> colType;
    @FXML private TableColumn<Room, String> colStatus;

    private final ObservableList<Room> rooms = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colRoom.setCellValueFactory(d -> d.getValue().roomNumberProperty());
        colType.setCellValueFactory(d -> d.getValue().categoryProperty());
        colStatus.setCellValueFactory(d -> d.getValue().statusProperty());

        cbType.setItems(FXCollections.observableArrayList("Single", "Double", "Suite"));
        cbStatus.setItems(FXCollections.observableArrayList("Empty", "Occupied", "Cleaning"));

        rooms.setAll(RoomRepository.getAll());
        tblRooms.setItems(rooms);
    }

    @FXML
    private void onAddRoom() {
        String roomNum = safe(txtRoomNumber.getText());
        String type    = cbType.getValue();
        String statusL = cbStatus.getValue();

        if (roomNum.isEmpty() || type == null || statusL == null) {
            showAlert("Please fill in all fields.");
            return;
        }

        Room room = new Room(
            null,         // id: DB sinh bằng fn_GenerateNextRoomID()
            type,         // category
            roomNum,      // roomNumber
            "Standard",   // quality mặc định
            0.0,          // price mặc định
            statusL       // status dạng String
        );

        RoomRepository.save(room);              // ghi DB (nhớ map String->int trong repo)
        rooms.setAll(RoomRepository.getAll());  // refresh bảng
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

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
