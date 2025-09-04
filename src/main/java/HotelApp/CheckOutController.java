package HotelApp;

import HotelApp.model.Booking;
import HotelApp.repository.BookingRepository;
import HotelApp.repository.RoomRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Check-out:
 * - Hiển thị các booking đã check-in (status = 4).
 * - Khi bấm Check Out: mở phòng về Room_status = 0 (Empty).
 * - Không đổi Booking_status (giữ 4) vì chưa dùng Staying_Management.
 */
public class CheckOutController {

    @FXML private TableView<Booking> tblBookings;
    @FXML private TableColumn<Booking, String> colGuest;
    @FXML private TableColumn<Booking, String> colRoom;
    @FXML private TableColumn<Booking, String> colCheckIn;
    @FXML private TableColumn<Booking, String> colCheckOut;
    @FXML private Button btnCheckOut;

    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colGuest.setCellValueFactory(d -> new SimpleStringProperty(
                safe(d.getValue().getGuestName())
        ));
        colRoom.setCellValueFactory(d -> new SimpleStringProperty(
                safe(d.getValue().getRoomNumber())
        ));
        colCheckIn.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCheckInDate() == null ? "" : d.getValue().getCheckInDate().toString()
        ));
        colCheckOut.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCheckOutDate() == null ? "" : d.getValue().getCheckOutDate().toString()
        ));

        // bật/tắt nút theo selection
        btnCheckOut.disableProperty().bind(
                tblBookings.getSelectionModel().selectedItemProperty().isNull()
        );

        refresh();
    }

    @FXML
    private void onCheckOut() {
        Booking selected = tblBookings.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String roomNum = safe(selected.getRoomNumber()).trim();
        if (!roomNum.isEmpty()) {
            // 0 = Empty
            boolean ok = RoomRepository.updateStatusByRoomNumber(roomNum, 0);
            if (!ok) {
                alert("Failed to set room empty: " + roomNum);
                return;
            }
        } else {
            alert("Missing room number.");
            return;
        }

        // Giữ Booking_status = 4 (đã nhận phòng). Nếu cần “đóng” booking, hãy thêm trạng thái riêng hoặc dùng Staying_Management.
        refresh();
        alert("Checked out room: " + roomNum);
    }

    private void refresh() {
        // Lấy toàn bộ booking và lọc 4 = Room received (đã check-in)
        bookings.setAll(
                BookingRepository.getAll().stream()
                        .filter(b -> b.getStatus() == 4)
                        .toList()
        );
        tblBookings.setItems(bookings);
        tblBookings.refresh();
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private void alert(String msg){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
