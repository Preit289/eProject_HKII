package HotelApp;

import HotelApp.repository.ServicesRepository;
import HotelApp.repository.RoomRepository;
import HotelApp.repository.BookingRepository;
import HotelApp.model.Room;
import HotelApp.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingController {

    @FXML
    private TextField txtGuestName;
    @FXML
    private TextField txtRoomNumber;
    @FXML
    private DatePicker dpCheckin;
    @FXML
    private DatePicker dpCheckout;

    @FXML
    private TableView<Booking> tblBookings;
    @FXML
    private TableColumn<Booking, String> colGuest;
    @FXML
    private TableColumn<Booking, String> colRoom;
    @FXML
    private TableColumn<Booking, LocalDate> colCheckin;
    @FXML
    private TableColumn<Booking, LocalDate> colCheckout;
    @FXML
    private TableColumn<Booking, Double> colPrice;

    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colGuest.setCellValueFactory(data -> data.getValue().guestNameProperty());
        colRoom.setCellValueFactory(data -> data.getValue().roomNumberProperty());
        colCheckin.setCellValueFactory(data -> data.getValue().checkInDateProperty());
        colCheckout.setCellValueFactory(data -> data.getValue().checkOutDateProperty());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());

        tblBookings.setItems(bookings);
    }

    @FXML
    private void onSave() {
        String guest = txtGuestName.getText();
        String roomNumber = txtRoomNumber.getText();
        LocalDate checkin = dpCheckin.getValue();
        LocalDate checkout = dpCheckout.getValue();

        if (guest.isBlank() || roomNumber.isBlank() || checkin == null || checkout == null) {
            showAlert("Please fill in all fields.");
            return;
        }
        if (checkout.isBefore(checkin)) {
            showAlert("Check-out date cannot be before Check-in date.");
            return;
        }

//        // Find the room by room number (assumes RoomRepository exists)
//        Room room = RoomRepository.findByRoomNumber(roomNumber);
//        if (room == null) {
//            showAlert("Room not found.");
//            return;
//        }
//
//        // Calculate price based on room type and duration
//        double pricePerNight = ServicesRepository.getPriceForRoomType(room.getType());
//        long nights = ChronoUnit.DAYS.between(checkin, checkout);
//        double totalPrice = pricePerNight * nights;
//
//        Booking newBooking = new Booking(guest, roomNumber, checkin, checkout, room);
//        newBooking.setPrice(totalPrice);
//        bookings.add(newBooking);
//        BookingRepository.save(newBooking); // Save to repository
//        clearForm();
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private void clearForm() {
        txtGuestName.clear();
        txtRoomNumber.clear();
        dpCheckin.setValue(null);
        dpCheckout.setValue(null);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
