package HotelApp;

import HotelApp.model.Booking;
import HotelApp.repository.BookingRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingController {

    @FXML private TextField txtGuestName;
    @FXML private TextField txtRoomNumber;        // có thể để trống
    @FXML private DatePicker dpCheckin;
    @FXML private DatePicker dpCheckout;

    @FXML private TableView<Booking> tblBookings;
    @FXML private TableColumn<Booking,String> colGuest;
    @FXML private TableColumn<Booking,String> colRoom;
    @FXML private TableColumn<Booking,String> colCheckin;
    @FXML private TableColumn<Booking,String> colCheckout;
    @FXML private TableColumn<Booking,String> colPrice;

    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGuestName()));
        colRoom.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getRoomNumber()==null ? "" : d.getValue().getRoomNumber()
        ));
        colCheckin.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getCheckInDate()==null ? "" : d.getValue().getCheckInDate().toString()
        ));
        colCheckout.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getCheckOutDate()==null ? "" : d.getValue().getCheckOutDate().toString()
        ));
        colPrice.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getPrice())));

        tblBookings.setItems(bookings);
        refresh();
    }

    @FXML
    private void onSave() {
        String guest = safe(txtGuestName.getText());
        String room  = safe(txtRoomNumber.getText()); // cho phép rỗng
        LocalDate ci = dpCheckin.getValue();
        LocalDate co = dpCheckout.getValue();

        if (guest.isEmpty() || ci == null || co == null) {
            alert("Please fill in required fields.");
            return;
        }
        if (co.isBefore(ci)) {
            alert("Check-out date cannot be before Check-in date.");
            return;
        }

        // kiểm tra trùng lịch nếu có nhập phòng
        if (!room.isEmpty() && BookingRepository.hasConflict(room, ci, co)) {
            alert("Room is not available for the selected dates.");
            return;
        }

        // nights chỉ để hiển thị tạm thời; giá thực tế tính ở getAll()
        long nights = Math.max(1, ChronoUnit.DAYS.between(ci, co));
        Booking b = new Booking(guest, room, ci, co, null);
        b.setPrice(0.0); // sẽ được tính lại từ DB khi refresh

        BookingRepository.save(b);
        refresh();
        clearForm();
    }

    private void refresh() {
        bookings.setAll(BookingRepository.getAll());
        tblBookings.refresh();
        System.out.println("Loaded rows = " + bookings.size());
    }

    @FXML
    private void onClear() { clearForm(); }

    private void clearForm() {
        txtGuestName.clear();
        txtRoomNumber.clear();
        dpCheckin.setValue(null);
        dpCheckout.setValue(null);
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
