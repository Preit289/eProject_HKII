package HotelApp;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CheckInController {

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
    private Button btnCheckIn;

    private ObservableList<Booking> bookings;

    @FXML
    private void initialize() {
        colGuest.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGuestName()));
        colRoom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomNumber()));
        colCheckIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInDate().toString()));
        colCheckOut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutDate().toString()));

        // Load only bookings with status = "Booked"
        bookings = FXCollections.observableArrayList(
                BookingRepository.getAll().stream()
                        .filter(b -> b.getStatus().equals("Booked"))
                        .toList()
        );
        tblBookings.setItems(bookings);
    }

    @FXML
    private void onCheckIn() {
        Booking selected = tblBookings.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Checked-in");
            Room room = selected.getRoom();
            if (room != null) {
                room.setStatus("Occupied");
            }
            tblBookings.getItems().remove(selected); // Remove from list
        }
    }
}
