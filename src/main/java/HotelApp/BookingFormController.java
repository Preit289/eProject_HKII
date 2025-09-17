package HotelApp;

import HotelApp.repository.BookingRepository;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;

public class BookingFormController {

    @FXML private Label lblTitle;
    @FXML private TextField txtBooker, txtPhone, txtDeposit;
    @FXML private ComboBox<String> cbPayment;
    @FXML private TableView<RoomVM> tblRooms;
    @FXML private TableColumn<RoomVM,String> colRoomNum, colCategory, colQuality;
    @FXML private TableColumn<RoomVM,Number> colPrice;
    @FXML private Button btnSave, btnDelete;
    @FXML private DatePicker dpCheckin, dpCheckout;

    private final BookingRepository repo = new BookingRepository();
    private String bookingId;                     
    private BookingController.FormMode mode;      

    @FXML
    private void initialize(){
        cbPayment.setItems(FXCollections.observableArrayList("Cash","Card","Transfer"));

        colRoomNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().num()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category()));
        colQuality.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().quality()));
        colPrice.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().price()));

        LocalDate today = LocalDate.now();
        dpCheckin.setDayCellFactory(disableBefore(today));
        dpCheckout.setDayCellFactory(picker -> new DateCell(){
            @Override public void updateItem(LocalDate item, boolean empty){
                super.updateItem(item, empty);
                LocalDate ci = dpCheckin.getValue() != null ? dpCheckin.getValue() : today;
                setDisable(empty || !item.isAfter(ci));
            }
        });
        dpCheckin.valueProperty().addListener((obs, o, n) -> dpCheckout.setValue(null));
    }

    private Callback<DatePicker, DateCell> disableBefore(LocalDate min){
        return picker -> new DateCell(){
            @Override public void updateItem(LocalDate item, boolean empty){
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(min));
            }
        };
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
            var dates = repo.getPlannedDates(bookingId);
            if(dates != null){
                dpCheckin.setValue(dates[0]);
                dpCheckout.setValue(dates[1]);
            }
            tblRooms.setItems(FXCollections.observableArrayList(repo.getRoomsVM(bookingId)));
        }else{
            tblRooms.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void onAddRoom(){
        LocalDate ci = dpCheckin.getValue();
        LocalDate co = dpCheckout.getValue();
        if(ci == null || co == null || !co.isAfter(ci)){
            new Alert(Alert.AlertType.WARNING, "Chọn ngày Check-in/Check-out hợp lệ trước khi thêm phòng.").showAndWait();
            return;
        }

        var options = repo.pickFreeRoomsVM(ci, co);
        if(options.isEmpty()){
            new Alert(Alert.AlertType.INFORMATION, "Không còn phòng trống trong khoảng ngày này.").showAndWait();
            return;
        }

        // Tạo dialog có checkbox để chọn nhiều phòng
        TableView<SelectableRoomVM> table = new TableView<>();
        table.setEditable(true);

        TableColumn<SelectableRoomVM, Boolean> colSelect = new TableColumn<>("Chọn");
        colSelect.setCellValueFactory(c -> c.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        colSelect.setPrefWidth(60);

        TableColumn<SelectableRoomVM, String> colLabel = new TableColumn<>("Phòng");
        colLabel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().label()));
        colLabel.setPrefWidth(300);

        table.getColumns().addAll(colSelect, colLabel);
        ObservableList<SelectableRoomVM> items = FXCollections.observableArrayList();
        for(var o : options){
            items.add(new SelectableRoomVM(o.id(), o.label()));
        }
        table.setItems(items);
        table.setPrefHeight(300);

        Dialog<List<SelectableRoomVM>> dlg = new Dialog<>();
        dlg.setTitle("Danh sách phòng");
        dlg.getDialogPane().setContent(table);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if(btn == ButtonType.OK){
                return items.filtered(SelectableRoomVM::isSelected);
            }
            return null;
        });

        var pickedList = dlg.showAndWait().orElse(null);
        if(pickedList == null || pickedList.isEmpty()) return;

        ensureCreatedIfNeeded(ci, co);
        for(var picked : pickedList){
            if(repo.isRoomAvailable(picked.id(), ci, co)){
                repo.addRoomToBooking(bookingId, picked.id());
            }
        }
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
        LocalDate ci  = dpCheckin.getValue();
        LocalDate co  = dpCheckout.getValue();

        if(ci == null || co == null || !co.isAfter(ci)){
            new Alert(Alert.AlertType.INFORMATION, "Chọn ngày Check-in/Check-out hợp lệ.").showAndWait();
            return;
        }

        if(mode == BookingController.FormMode.UPDATE){
            repo.updateBooking(new BookingDTO(bookingId, booker, phone, deposit, pay), ci, co);
        }else{
            bookingId = repo.createBooking(new BookingDTO(null, booker, phone, deposit, pay), ci, co);
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

    private void ensureCreatedIfNeeded(LocalDate ci, LocalDate co){
        if(mode == BookingController.FormMode.CREATE){
            bookingId = repo.createBooking(new BookingDTO(
                    null, txtBooker.getText().trim(), txtPhone.getText().trim(),
                    parseIntSafe(txtDeposit.getText().trim(), 0), cbPayment.getValue()
            ), ci, co);
            mode = BookingController.FormMode.UPDATE;
            btnSave.setText("Update");
            btnDelete.setDisable(false);
            lblTitle.setText("Update Booking");
        }
    }

    private int parseIntSafe(String s, int def){ try{ return Integer.parseInt(s); }catch(Exception e){ return def; } }

    public record RoomVM(String id, String num, String category, String quality, int price) {}
    public record BookingDTO(String id, String booker, String phone, int deposit, String payment) {}
    public record RoomPickVM(String id, String label){ @Override public String toString(){ return label; } }

    public static class SelectableRoomVM {
        private final String id;
        private final String label;
        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        public SelectableRoomVM(String id, String label) {
            this.id = id; this.label = label;
        }
        public String id(){ return id; }
        public String label(){ return label; }
        public BooleanProperty selectedProperty(){ return selected; }
        public boolean isSelected(){ return selected.get(); }
    }
}
