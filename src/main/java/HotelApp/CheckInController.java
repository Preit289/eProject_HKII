package HotelApp;

import HotelApp.repository.CheckinRepository;
import HotelApp.model.Checkin;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private TextField txtSearchPhone;

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HotelApp/CheckInForm.fxml"));
            Parent formRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Check-in Form");
            stage.setScene(new Scene(formRoot));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not open check-in form.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onSearch(ActionEvent event) {
        String phone = txtSearchPhone.getText().trim();

        if (phone.isEmpty()) {
            // Reload all bookings if no search input
            tblBookings.setItems(CheckinRepository.getAllBookings());
        } else {
            // Filter by phone
            tblBookings.setItems(CheckinRepository.searchBookingsByPhone(phone));
        }
    }
}
