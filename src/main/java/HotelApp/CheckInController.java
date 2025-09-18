package HotelApp;

import HotelApp.model.Checkin;
import HotelApp.repository.CheckinRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import javafx.scene.layout.VBox;

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
    private Button btnSearch;
    @FXML
    private Button btnCheckin;
    @FXML
    private Button btnUpdate;

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

        // Set single selection mode
        tblBookings.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Disable buttons when no row is selected
        btnCheckin.disableProperty().bind(
                tblBookings.getSelectionModel().selectedItemProperty().isNull()
        );
        btnUpdate.disableProperty().bind(
                tblBookings.getSelectionModel().selectedItemProperty().isNull()
        );

        // Add double-click handler to open check-in form
        tblBookings.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tblBookings.getSelectionModel().isEmpty()) {
                onUpdate();
            }
        });
    }

    @FXML
    private void onCheckin() {
        Checkin selectedBooking = tblBookings.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a booking to check in.");
            alert.showAndWait();
            return;
        }

        try {
            // Create or get existing Staying_id
            String stayingId = CheckinRepository.createStayingFromBooking(selectedBooking);

            // Get current Checkin_date
            String currentCheckinDate = CheckinRepository.getCheckinDate(stayingId);
            String displayDate = currentCheckinDate.isEmpty() ? "Not set" : currentCheckinDate;

            // Show confirmation dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Confirm Check-in");
            dialog.setHeaderText("Confirm check-in for " + selectedBooking.getGuestName());
            dialog.getDialogPane().setContent(
                    new VBox(10, new Label("Current Check-in Date: " + displayDate),
                            new Label("New Check-in Date: " + java.time.LocalDateTime.now().toString()))
            );
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Perform check-in
                        CheckinRepository.performCheckin(stayingId);

                        // Show success message
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "Check-in performed successfully for " + selectedBooking.getGuestName());
                        alert.showAndWait();

                        // Refresh the table
                        tblBookings.setItems(CheckinRepository.getAllBookings());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Failed to perform check-in: " + e.getMessage());
                        alert.showAndWait();
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create or access stay: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onUpdate() {
        Checkin selectedBooking = tblBookings.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a booking to update.");
            alert.showAndWait();
            return;
        }

        try {
            // Create or get existing Staying_id
            String stayingId = CheckinRepository.createStayingFromBooking(selectedBooking);

            // Load the CheckInForm
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HotelApp/CheckInForm.fxml"));
            Parent formRoot = loader.load();

            // Pass data to CheckInFormController
            CheckInFormController controller = loader.getController();
            controller.setBookingData(selectedBooking, stayingId);

            Stage stage = new Stage();
            stage.setTitle("Check-in Form");
            stage.setScene(new Scene(formRoot));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the table after update
            tblBookings.setItems(CheckinRepository.getAllBookings());

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create or access stay: " + e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not open check-in form: " + e.getMessage());
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
