package HotelApp;

import HotelApp.repository.BookingRepository;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BookingFormController {

    @FXML private Label lblTitle;
    @FXML private TextField txtBooker, txtPhone, txtDeposit;
    @FXML private ComboBox<String> cbPayment;
    @FXML private TableView<RoomVM> tblRooms;
    @FXML private TableColumn<RoomVM,String> colRoomNum, colCategory, colQuality;
    @FXML private TableColumn<RoomVM,Number> colPrice;
    @FXML private Button btnSave, btnDelete;

    private final BookingRepository repo = new BookingRepository();
    private String bookingId;                     // null nếu CREATE
    private BookingController.FormMode mode;      // CREATE / UPDATE

    @FXML
    private void initialize(){
        cbPayment.setItems(FXCollections.observableArrayList("Cash","Card","Transfer"));
        colRoomNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().num()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category()));
        colQuality.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().quality()));
        colPrice.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().price()));
    }

    public void init(BookingController.FormMode mode, String bookingId){
        this.mode = mode;
        this.bookingId = bookingId;
        boolean isUpdate = mode == BookingController.FormMode.UPDATE;

        btnSave.setText(isUpdate ? "Update" : "Create");
        btnDelete.setDisable(!isUpdate);
        lblTitle.setText(isUpdate ? "Update Booking" : "New Booking");

        if(isUpdate){
            var b = repo.getById(bookingId);
            if(b != null){
                txtBooker.setText(b.booker());
                txtPhone.setText(b.phone());
                cbPayment.getSelectionModel().select(b.payment());
                txtDeposit.setText(String.valueOf(b.deposit()));
            }
            tblRooms.setItems(FXCollections.observableArrayList(repo.getRoomsVM(bookingId)));
        }else{
            tblRooms.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void onAddRoom(){
        var options = repo.pickFreeRoomsVM();
        if(options.isEmpty()) return;
        ChoiceDialog<RoomPickVM> dlg = new ChoiceDialog<>(options.get(0), options);
        dlg.setTitle("Pick a room"); dlg.setHeaderText("Select a free room");
        var picked = dlg.showAndWait().orElse(null);
        if(picked == null) return;

        ensureCreatedIfNeeded();
        repo.addRoomToBooking(bookingId, picked.id());
        tblRooms.setItems(FXCollections.observableArrayList(repo.getRoomsVM(bookingId)));
    }

    @FXML
    private void onRemoveRoom(){
        var sel = tblRooms.getSelectionModel().getSelectedItem();
        if(sel == null || bookingId == null) return;
        repo.removeRoomFromBooking(bookingId, sel.id());
        tblRooms.getItems().remove(sel);
    }

    @FXML
    private void onSave(){
        String booker = txtBooker.getText().trim();
        String phone  = txtPhone.getText().trim();
        String pay    = cbPayment.getValue();
        int deposit   = parseIntSafe(txtDeposit.getText().trim(), 0);

        if(mode == BookingController.FormMode.UPDATE){
            repo.updateBooking(new BookingDTO(bookingId, booker, phone, deposit, pay));
        }else{
            bookingId = repo.createBooking(new BookingDTO(null, booker, phone, deposit, pay));
            mode = BookingController.FormMode.UPDATE;
            btnSave.setText("Update");
            btnDelete.setDisable(false);
            lblTitle.setText("Update Booking");
        }
        if(bookingId != null){
            tblRooms.setItems(FXCollections.observableArrayList(repo.getRoomsVM(bookingId)));
        }
        close();
    }

    @FXML private void onDelete(){ if(bookingId != null) repo.deleteBooking(bookingId); close(); }
    @FXML private void onClose(){ close(); }
    private void close(){ ((Stage) txtBooker.getScene().getWindow()).close(); }

    private void ensureCreatedIfNeeded(){
        if(mode == BookingController.FormMode.CREATE){
            onSave(); // tạo booking trước khi gán phòng
        }
    }
    private int parseIntSafe(String s, int def){ try{ return Integer.parseInt(s); }catch(Exception e){ return def; } }

    public record RoomVM(String id, String num, String category, String quality, int price) {}
    public record BookingDTO(String id, String booker, String phone, int deposit, String payment) {}
    public record RoomPickVM(String id, String label){ @Override public String toString(){ return label; } }
}
