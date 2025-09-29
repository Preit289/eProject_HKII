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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

public class CheckInController {

    @FXML private TableView<Checkin> tblBookings;
    @FXML private TableColumn<Checkin, String> colGuestName;
    @FXML private TableColumn<Checkin, String> colGuestPhone;
    @FXML private TableColumn<Checkin, String> colRoomNumbers;
    @FXML private TableColumn<Checkin, String> colRoomCategory;
    @FXML private TableColumn<Checkin, String> colRoomType;
    @FXML private TableColumn<Checkin, String> colCheckIn;
    @FXML private TableColumn<Checkin, String> colCheckOut;
    @FXML private TextField txtSearchPhone;
    @FXML private Button btnSearch;
    @FXML private Button btnCheckin;
    @FXML private Button btnUpdate;

    private final DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private void initialize() {
        // Bind columns
        colGuestName.setCellValueFactory(c -> c.getValue().guestNameProperty());
        colGuestPhone.setCellValueFactory(c -> c.getValue().guestPhoneProperty());
        colRoomNumbers.setCellValueFactory(c -> c.getValue().roomNumbersProperty());
        colRoomCategory.setCellValueFactory(c -> c.getValue().roomCategoryProperty());
        colRoomType.setCellValueFactory(c -> c.getValue().roomTypeProperty());

        // Format check-in / check-out to yyyy-MM-dd
        colCheckIn.setCellValueFactory(c -> formatDate(c.getValue().plannedCheckInProperty().get()));
        colCheckOut.setCellValueFactory(c -> formatDate(c.getValue().plannedCheckOutProperty().get()));

        // Load data
        loadTable();

        // Single selection
        tblBookings.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Disable buttons when no selection
        btnCheckin.disableProperty().bind(tblBookings.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(tblBookings.getSelectionModel().selectedItemProperty().isNull());

        // Double click opens update form
        tblBookings.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tblBookings.getSelectionModel().isEmpty()) {
                onUpdate();
            }
        });
    }

    // Format String -> SimpleStringProperty
    private SimpleStringProperty formatDate(String raw) {
        if (raw == null || raw.isBlank()) return new SimpleStringProperty("");
        try {
            LocalDateTime dt = LocalDateTime.parse(raw);
            return new SimpleStringProperty(fmtDate.format(dt));
        } catch (Exception e) {
            return new SimpleStringProperty(raw.split("T")[0]);
        }
    }

    private void loadTable() {
        tblBookings.getItems().clear();
        tblBookings.getItems().addAll(CheckinRepository.getAllBookings());
        tblBookings.refresh();
    }

    @FXML
    private void onCheckin() {
        Checkin selectedBooking = tblBookings.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a booking to check in.").showAndWait();
            return;
        }

        try {
            String stayingId = CheckinRepository.createStayingFromBooking(selectedBooking);

            String currentCheckinDate = CheckinRepository.getCheckinDate(stayingId);
            String displayDate = currentCheckinDate.isEmpty() ? "Not set" : currentCheckinDate;

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Confirm Check-in");
            dialog.setHeaderText("Confirm check-in for " + selectedBooking.getGuestName());
            dialog.getDialogPane().setContent(
                    new VBox(10,
                        new Label("Current Check-in Date: " + displayDate),
                        new Label("New Check-in Date: " + LocalDateTime.now().format(fmtDate))
                    )
            );
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        CheckinRepository.performCheckin(stayingId);
                        new Alert(Alert.AlertType.INFORMATION,
                                "Check-in performed successfully for " + selectedBooking.getGuestName()).showAndWait();
                        loadTable(); // refresh table
                    } catch (SQLException e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR,
                                "Failed to perform check-in: " + e.getMessage()).showAndWait();
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to create or access stay: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onUpdate() {
        Checkin selectedBooking = tblBookings.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a booking to update.").showAndWait();
            return;
        }

        try {
            String stayingId = CheckinRepository.createStayingFromBooking(selectedBooking);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HotelApp/CheckInForm.fxml"));
            Parent root = loader.load();
            CheckInFormController controller = loader.getController();
            controller.setBookingData(selectedBooking, stayingId);

            Stage stage = new Stage();
            stage.setTitle("Check-in Form");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadTable(); // refresh table after update

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onSearch(ActionEvent event) {
        String phone = txtSearchPhone.getText().trim();
        if (phone.isEmpty()) {
            loadTable();
        } else {
            tblBookings.getItems().clear();
            tblBookings.getItems().addAll(CheckinRepository.searchBookingsByPhone(phone));
            tblBookings.refresh();
        }
    }
}
