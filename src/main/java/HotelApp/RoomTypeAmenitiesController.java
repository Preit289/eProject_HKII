package HotelApp;

import HotelApp.model.RoomTypeAmenity;
import HotelApp.repository.RoomTypeAmenityRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RoomTypeAmenitiesController {

    @FXML
    private TableView<RoomTypeAmenity> tblRoomTypes;
    @FXML
    private TableColumn<RoomTypeAmenity, String> colCategory;
    @FXML
    private TableColumn<RoomTypeAmenity, String> colQuality;
    @FXML
    private TableColumn<RoomTypeAmenity, Integer> colCapacity;
    @FXML
    private TableColumn<RoomTypeAmenity, String> colAmenities;

    @FXML
    private ChoiceBox<String> cbCategory;
    @FXML
    private ChoiceBox<String> cbQuality;
    @FXML
    private TextField txtCapacity;
    @FXML
    private TextArea txtAreaAmenities;

    private final ObservableList<RoomTypeAmenity> roomTypes = FXCollections.observableArrayList();
    private RoomTypeAmenity selectedRoomType;

    @FXML
    private void initialize() {
        setupTableColumns();
        loadRoomTypes();
        setupTableSelection();
    }

    private void setupTableColumns() {
        colCategory.setCellValueFactory(data -> data.getValue().categoryProperty());
        colQuality.setCellValueFactory(data -> data.getValue().qualityProperty());
        colCapacity.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCapacity()));
        colAmenities.setCellValueFactory(data -> data.getValue().amenitiesProperty());
    }

    private void loadRoomTypes() {
        roomTypes.clear();
        roomTypes.addAll(RoomTypeAmenityRepository.getAll());
        tblRoomTypes.setItems(roomTypes);
    }

    private void setupTableSelection() {
        tblRoomTypes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedRoomType = newSelection;
                showDetails(selectedRoomType);
            }
        });
    }

    private void showDetails(RoomTypeAmenity roomType) {
        cbCategory.setValue(roomType.getCategory());
        cbQuality.setValue(roomType.getQuality());
        txtCapacity.setText(String.valueOf(roomType.getCapacity()));
        txtAreaAmenities.setText(roomType.getAmenities());
    }

    @FXML
    private void onAdd() {
        try {
            validateInput();
            RoomTypeAmenity newRoomType = new RoomTypeAmenity(
                    cbCategory.getValue(),
                    cbQuality.getValue(),
                    Integer.parseInt(txtCapacity.getText()),
                    txtAreaAmenities.getText()
            );

            if (RoomTypeAmenityRepository.add(newRoomType)) {
                loadRoomTypes();
                onClear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room type added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add room type");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        if (selectedRoomType == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a room type to update");
            return;
        }

        try {
            validateInput();
            RoomTypeAmenity updatedRoomType = new RoomTypeAmenity(
                    cbCategory.getValue(),
                    cbQuality.getValue(),
                    Integer.parseInt(txtCapacity.getText()),
                    txtAreaAmenities.getText()
            );

            if (RoomTypeAmenityRepository.update(updatedRoomType)) {
                loadRoomTypes();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room type updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update room type");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        if (selectedRoomType == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a room type to delete");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Room Type");
        confirmDialog.setContentText("Are you sure you want to delete this room type?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            if (RoomTypeAmenityRepository.delete(selectedRoomType.getCategory(), selectedRoomType.getQuality())) {
                loadRoomTypes();
                onClear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room type deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete room type");
            }
        }
    }

    @FXML
    private void onClear() {
        cbCategory.setValue(null);
        cbQuality.setValue(null);
        txtCapacity.clear();
        txtAreaAmenities.clear();
        selectedRoomType = null;
        tblRoomTypes.getSelectionModel().clearSelection();
    }

    private void validateInput() throws Exception {
        if (cbCategory.getValue() == null) {
            throw new Exception("Room category is required");
        }
        if (cbQuality.getValue() == null) {
            throw new Exception("Quality level is required");
        }
        if (txtCapacity.getText().trim().isEmpty()) {
            throw new Exception("Capacity is required");
        }
        try {
            Integer.parseInt(txtCapacity.getText());
        } catch (NumberFormatException e) {
            throw new Exception("Capacity must be a number");
        }
        if (txtAreaAmenities.getText().trim().isEmpty()) {
            throw new Exception("Amenities are required");
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
