package HotelApp;

import HotelApp.model.Booking;
import HotelApp.model.Room;
import HotelApp.repository.BookingRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.print.PrinterJob;
import javafx.scene.control.Separator;
import javafx.geometry.Insets;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CheckOutController {

    @FXML
    private TableView<Booking> tblBookings;
    @FXML
    private TableColumn<Booking, String> colGuest;
    @FXML
    private TableColumn<Booking, String> colRoom;
    @FXML
    private TableColumn<Booking, String> colCheckIn;
    @FXML
    private TableColumn<Booking, String> colCheckOut;
    @FXML
    private TableColumn<Booking, String> colCreatedAt; 
    @FXML
    private Button btnCheckOut;

    private ObservableList<Booking> bookings;

    @FXML
    private void initialize() {
        // Set up TableView columns
        colGuest.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGuestName()));
        colRoom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomNumber()));
        colCheckIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInDate().toString()));
        colCheckOut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutDate().toString()));
        colCreatedAt.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt().toString()));

        // Load checked-in bookings
        List<Booking> checkedIn = BookingRepository.getAll().stream()
                .filter(b -> "Checked-in".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());
        bookings = FXCollections.observableArrayList(checkedIn);
        tblBookings.setItems(bookings);

        // Disable button if no selection
        tblBookings.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnCheckOut.setDisable(newSel == null);
        });
        btnCheckOut.setDisable(true); // initial
    }

    @FXML
    private void onCheckOut() {
        Booking selected = tblBookings.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Update status
            selected.setStatus("Checked-out");

            // Update room status if exists
            Room room = selected.getRoom();
            if (room != null) {
                room.setStatus("Available");
            }

            // Remove from TableView
            bookings.remove(selected);

            // Optionally, update repository/database
            BookingRepository.update(selected);

            // Optional: feedback
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Check-Out Successful");
            alert.setHeaderText(null);
            alert.setContentText("Guest " + selected.getGuestName() + " has checked out successfully.");
            alert.showAndWait();
        }
    }
}

