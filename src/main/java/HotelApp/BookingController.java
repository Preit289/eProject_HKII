package HotelApp;

import HotelApp.repository.BookingRepository;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BookingController {

    @FXML private TextField txtSearch;
    @FXML private TableView<BookingVM> tblBooking;
    @FXML private TableColumn<BookingVM,String> colId, colBooker, colPhone, colPay;
    @FXML private TableColumn<BookingVM,Number>  colDeposit, colRooms;

    private final BookingRepository repo = new BookingRepository();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().id()));
        colBooker.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().booker()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().phone()));
        colPay.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().payment()));
        colDeposit.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().deposit()));
        colRooms.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().rooms()));

        // Gắn nút More ở cuối mỗi dòng trong cột Rooms
        colRooms.setCellFactory(col -> new TableCell<>() {
            private final Label lbl = new Label();
            private final Pane spacer = new Pane();
            private final Button more = new Button("More");
            private final HBox box = new HBox(8, lbl, spacer, more);
            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                more.setOnAction(e -> {
                    var vm = getTableView().getItems().get(getIndex());
                    openDetail(FormMode.UPDATE, vm.id());
                });
            }
            @Override protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty) { setGraphic(null); setText(null); }
                else { lbl.setText(value == null ? "0" : String.valueOf(value.intValue())); setGraphic(box); setText(null); }
            }
        });

        load("");
    }

    @FXML private void onSearch(ActionEvent e){ load(txtSearch.getText().trim()); }
    @FXML private void onCreate(ActionEvent e){ openDetail(FormMode.CREATE, null); }

    private void load(String keyword){
        try {
            var rows = FXCollections.observableArrayList(repo.searchVM(keyword));
            tblBooking.setItems(rows);
            if (rows.isEmpty()) System.out.println("Booking: 0 rows");
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Load booking failed:\n"+ex.getMessage()).showAndWait();
        }
    }

    private void openDetail(FormMode mode, String bookingId){
        try{
            FXMLLoader f = new FXMLLoader(getClass().getResource("/HotelApp/BookingForm.fxml"));
            Parent root = f.load();
            BookingFormController c = f.getController();
            c.init(mode, bookingId); // CREATE=null, UPDATE=bookingId
            Stage s = new Stage();
            s.initModality(Modality.APPLICATION_MODAL);
            s.setTitle(mode==FormMode.CREATE? "New Booking" : "Update Booking");
            s.setScene(new Scene(root));
            s.showAndWait();
            load(txtSearch.getText().trim());
        }catch(Exception ex){ ex.printStackTrace(); }
    }

    public enum FormMode { CREATE, UPDATE }
    public record BookingVM(String id, String booker, String phone, int deposit, String payment, int rooms) {}
}
