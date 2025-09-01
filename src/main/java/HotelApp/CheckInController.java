package HotelApp;

import HotelApp.model.Booking;
import HotelApp.repository.BookingRepository;
import HotelApp.repository.RoomRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CheckInController {

    @FXML private TableView<Booking> tblBookings;
    @FXML private TableColumn<Booking, String> colGuest;
    @FXML private TableColumn<Booking, String> colRoom;
    @FXML private TableColumn<Booking, String> colCheckIn;
    @FXML private TableColumn<Booking, String> colCheckOut;
    @FXML private Button btnCheckIn;

    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGuestName()));
        colRoom.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getRoomNumber() == null ? "" : d.getValue().getRoomNumber()
        ));
        colCheckIn.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getCheckInDate() == null ? "" : d.getValue().getCheckInDate().toString()
        ));
        colCheckOut.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getCheckOutDate() == null ? "" : d.getValue().getCheckOutDate().toString()
        ));

        refresh();
    }

    @FXML
    private void onCheckIn() {
        Booking selected = tblBookings.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // 4 = Room received (checked-in). 1 = Occupied cho phòng.
        BookingRepository.updateStatus(selected.getBookingId(), 4);
        String roomNum = selected.getRoomNumber();
        if (roomNum != null && !roomNum.isBlank()) {
            RoomRepository.updateStatusByRoomNumber(roomNum.trim(), 1);
        }

        refresh();
    }

    private void refresh() {
        bookings.setAll(
            BookingRepository.getAll().stream()
                .filter(b -> b.getStatus() == 2) // 2 = Booking active (Booked)
                .toList()
        );
        tblBookings.setItems(bookings);
        tblBookings.refresh();
    }
}
