package HotelApp;

import HotelApp.repository.CheckinRepository;
import HotelApp.model.Checkin;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class CheckInController {

    @FXML
    private TableView<Checkin> tblBookings;
    @FXML
    private TableColumn<Checkin, String> colGuestName;
    @FXML
    private TableColumn<Checkin, String> colGuestPhone;
    @FXML
    private TableColumn<Checkin, String> colRoomNumbers;
    @FXML
    private TableColumn<Checkin, String> colRoomCategory;
    @FXML
    private TableColumn<Checkin, String> colRoomType;
    @FXML
    private TableColumn<Checkin, String> colCheckIn;
    @FXML
    private TableColumn<Checkin, String> colCheckOut;

    @FXML
    private void initialize() {
        // Bind columns to StringProperty from Checkin model
        colGuestName.setCellValueFactory(cellData -> cellData.getValue().guestNameProperty());
        colGuestPhone.setCellValueFactory(cellData -> cellData.getValue().guestPhoneProperty());
        colRoomNumbers.setCellValueFactory(cellData -> cellData.getValue().roomNumbersProperty());
        colRoomCategory.setCellValueFactory(cellData -> cellData.getValue().roomCategoryProperty());
        colRoomType.setCellValueFactory(cellData -> cellData.getValue().roomTypeProperty());
        colCheckIn.setCellValueFactory(cellData -> cellData.getValue().plannedCheckInProperty());
        colCheckOut.setCellValueFactory(cellData -> cellData.getValue().plannedCheckOutProperty());

        // Load data from repository
        tblBookings.setItems(CheckinRepository.getAllBookings());
    }

    @FXML
    private void onCheckIn() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Check-in Result");
        alert.setHeaderText("Check-in Completed");
        alert.setContentText("Check-in button clicked! Display results here.");
        alert.showAndWait();
    }

    @FXML
    private void onSearch(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search Result");
        alert.setHeaderText("Search Completed");
        alert.setContentText("Search button clicked! Display results here.");
        alert.showAndWait();
    }
}
