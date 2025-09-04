package HotelApp;

import HotelApp.repository.RoomRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BookingController {

    @FXML private ChoiceBox<String> cbCapacity, cbType;
    @FXML private DatePicker dpCheckin, dpCheckout;

    @FXML private TableView<Map<String,Object>> tblRooms;
    @FXML private TableColumn<Map<String,Object>, String> colRoomNum, colCapacity, colType,
            colNearestIn, colNearestOut, colPrice, colAvail;

    private final ObservableList<Map<String,Object>> rows = FXCollections.observableArrayList();

    @FXML private void initialize() {
        cbCapacity.setItems(FXCollections.observableArrayList("1","2","3","4"));
        cbType.setItems(FXCollections.observableArrayList("Standard","VIP","Suite"));

        colRoomNum.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("room_num"))));
        colCapacity.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("capacity"))));
        colType.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("type"))));
        colNearestIn.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("near_in"))));
        colNearestOut.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("near_out"))));
        colPrice.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("price"))));
        colAvail.setCellValueFactory(d -> new SimpleStringProperty(s(d.getValue().get("availability"))));

        tblRooms.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblRooms.setItems(rows);
        onFilter();
    }

    @FXML private void onFilter() {
        Integer cap = cbCapacity.getValue()==null ? null : Integer.valueOf(cbCapacity.getValue());
        String type = cbType.getValue();
        LocalDate ci = dpCheckin.getValue();
        LocalDate co = dpCheckout.getValue();
        rows.setAll(RoomRepository.searchRooms(cap, type, ci, co));
    }

    /** Mở BookingForm và truyền username/roleText từ hệ login bên ngoài */
    @FXML private void onBook() throws Exception {
        List<Map<String,Object>> sel = tblRooms.getSelectionModel().getSelectedItems();
        if (sel==null || sel.isEmpty()) { alert("Select room(s)."); return; }
        if (dpCheckin.getValue()==null || dpCheckout.getValue()==null || !dpCheckout.getValue().isAfter(dpCheckin.getValue())) {
            alert("Invalid dates."); return;
        }

        // TODO: lấy từ module Account
        String currentUsername = "lee";
        String roleText = "Manager";

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/HotelApp/BookingForm.fxml"));
        Parent root = loader.load();
        BookingFormController ctrl = loader.getController();
        ctrl.setContext(sel, dpCheckin.getValue(), dpCheckout.getValue(), currentUsername, roleText);

        Stage stage = new Stage();
        stage.setTitle("Booking form");
        stage.setScene(new Scene(root));
        stage.initOwner(tblRooms.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();

        onFilter();
    }

    private static String s(Object o){ return o==null? "" : String.valueOf(o); }
    private void alert(String msg){ var a=new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}
