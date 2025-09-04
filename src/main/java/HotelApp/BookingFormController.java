package HotelApp;

import HotelApp.repository.BookingRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class BookingFormController {

    @FXML private Label lblDate, lblUser;
    @FXML private TableView<Map<String,Object>> tblRooms;
    @FXML private TableColumn<Map<String,Object>, String> colRoom, colCap, colType, colIn, colOut, colPrice, colDaysX;

    @FXML private TextField txtCustomerName, txtPhone, txtIdCard, txtEmail;
    @FXML private TextArea txNotes;
    @FXML private ChoiceBox<Integer> cbChildren;
    @FXML private ChoiceBox<String> cbPayment, cbReserveOrCheckin;

    @FXML private Label lblVat, lblTotal, lblDeposit;

    private final ObservableList<Map<String,Object>> rows = FXCollections.observableArrayList();
    private String currentUser;              // dùng cho Book_by & By_role
    private String roleText;                 // chỉ để hiển thị
    private List<String> selectedRoomNums;
    private LocalDate plannedIn, plannedOut;

    /** Nhận username/roleText từ BookingController (không dùng Session) */
    public void setContext(List<Map<String,Object>> selectedRooms,
                           LocalDate ci, LocalDate co,
                           String username, String roleText) {
        this.currentUser = username;
        this.roleText = roleText;
        this.selectedRoomNums = selectedRooms.stream().map(m -> String.valueOf(m.get("room_num"))).toList();
        this.plannedIn = ci; this.plannedOut = co;

        rows.setAll(selectedRooms);
        lblDate.setText(LocalDate.now().toString());
        lblUser.setText(username + " | " + roleText);
        recalcMoney();
    }

    @FXML private void initialize() {
        cbChildren.setItems(FXCollections.observableArrayList(0,1,2,3,4));
        cbPayment.setItems(FXCollections.observableArrayList("Cash","Card","Transfer"));
        cbReserveOrCheckin.setItems(FXCollections.observableArrayList("Reserve","Check-in"));

        colRoom.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("room_num"))));
        colCap .setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("capacity"))));
        colType.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("type"))));
        colIn  .setCellValueFactory(d -> new SimpleStringProperty(plannedIn==null?"":plannedIn.toString()));
        colOut .setCellValueFactory(d -> new SimpleStringProperty(plannedOut==null?"":plannedOut.toString()));
        colPrice.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("price"))));
        colDaysX.setCellValueFactory(d -> {
            long days = Math.max(1, ChronoUnit.DAYS.between(plannedIn, plannedOut));
            return new SimpleStringProperty(days + " × " + s(d.getValue().get("price")));
        });

        tblRooms.setItems(rows);
    }

    @FXML private void onReserve() { save(2); }  // 2 = ACTIVE
    @FXML private void onCheckin() { save(4); }  // 4 = RECEIVED

    private void save(int status) {
        if (!validate()) return;

        String bookingId = BookingRepository.createWithRooms(
                selectedRoomNums,
                plannedIn, plannedOut,
                parse(lblDeposit.getText()),
                cbPayment.getValue()==null ? "Cash" : cbPayment.getValue(),
                status,
                currentUser,                  // Book_by & By_role
                txtPhone.getText().trim()     // Book_contact (người đặt từ input)
        );

        if (bookingId == null) { alert("Room conflict or DB error."); return; }
        alert((status==4? "Checked in: " : "Reserved: ") + bookingId);
        lblUser.getScene().getWindow().hide();
    }

    private boolean validate() {
        if (plannedIn == null || plannedOut == null || !plannedOut.isAfter(plannedIn)) { alert("Invalid dates"); return false; }
        if (rows.isEmpty()) { alert("No room selected"); return false; }
        if (txtCustomerName.getText()==null || txtCustomerName.getText().isBlank()) { alert("Enter customer name"); return false; }
        if (txtPhone.getText()==null || txtPhone.getText().isBlank()) { alert("Enter phone"); return false; }
        return true;
    }

    private static String s(Object o){ return o==null? "" : String.valueOf(o); }
    private static String fmt(long v){ return String.format("%,d", v); }
    private static int parse(String s){ return Integer.parseInt(s.replaceAll("[^0-9]", "")); }
    private void alert(String m){ var a=new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(null); a.setContentText(m); a.showAndWait(); }
    private void recalcMoney() {
        long days = Math.max(1, ChronoUnit.DAYS.between(plannedIn, plannedOut));
        long sum = 0;
        for (var m : rows) {
            String p = s(m.get("price")).replaceAll("[^0-9.]", "");
            sum += Math.round(Double.parseDouble(p) * days);
        }
        long vat = Math.round(sum * 0.10);
        long deposit = Math.round(sum * 0.30);
        lblVat.setText(fmt(vat)); lblTotal.setText(fmt(sum)); lblDeposit.setText(fmt(deposit));
    }
}
