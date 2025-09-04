package HotelApp;

import HotelApp.repository.RoomRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomMgmtController {

    // ======= TABLE =======
    @FXML private TableView<RoomRow> tblRooms;
    @FXML private TableColumn<RoomRow, String> colNum;
    @FXML private TableColumn<RoomRow, String> colType;
    @FXML private TableColumn<RoomRow, String> colCap;
    @FXML private TableColumn<RoomRow, String> colStatus;
    @FXML private TableColumn<RoomRow, Number> colRate;
    @FXML private TableColumn<RoomRow, LocalDate> colNearIn;
    @FXML private TableColumn<RoomRow, LocalDate> colNearOut;

    // ======= FORM =======
    @FXML private TextField txtRoomNum;
    @FXML private TextField txtRate;
    @FXML private TextField txtCapacity;
    @FXML private ComboBox<String> cbType;
    @FXML private ComboBox<String> cbStatus;
    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;
    @FXML private TextArea txFacilities;
    @FXML private TextField txtSearch;

    private final ObservableList<RoomRow> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // table bindings
        colNum.setCellValueFactory(c -> new SimpleStringProperty(s(c.getValue().roomNum())));
        colType.setCellValueFactory(c -> new SimpleStringProperty(s(c.getValue().type())));
        colCap.setCellValueFactory(c -> new SimpleStringProperty(s(c.getValue().capacity())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(s(c.getValue().statusText())));
        colRate.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().price()));
        colNearIn.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().nearIn()));
        colNearOut.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().nearOut()));

        tblRooms.setItems(rows);
        tblRooms.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) fillForm(n);
        });

        // combos
        cbStatus.setItems(FXCollections.observableArrayList("Empty", "Occupied", "Cleaning"));
        cbType.setItems(FXCollections.observableArrayList("Single", "Double", "Family", "Suite"));

        // datepickers format
        StringConverter<LocalDate> cvt = new StringConverter<>() {
            @Override public String toString(LocalDate date) { return date == null ? "" : date.toString(); }
            @Override public LocalDate fromString(String s) { return (s == null || s.isBlank()) ? null : LocalDate.parse(s); }
        };
        dpFrom.setConverter(cvt);
        dpTo.setConverter(cvt);

        // initial load
        reloadAll();
    }

    private void reloadAll() {
        rows.setAll(mapToRows(RoomRepository.searchRooms(null, null, null, null)));
    }

    private List<RoomRow> mapToRows(List<Map<String, Object>> data) {
        List<RoomRow> list = new ArrayList<>();
        if (data == null) return list;
        for (Map<String, Object> m : data) {
            String num = s(m.get("room_num"));
            String type = s(m.get("type"));
            String cap = s(m.get("capacity"));
            String status = s(m.get("availability"));
            double price = asDouble(m.get("price"));
            LocalDate nearIn = asDate(m.get("near_in"));
            LocalDate nearOut = asDate(m.get("near_out"));
            list.add(new RoomRow(num, type, cap, status, price, nearIn, nearOut));
        }
        return list;
    }

    private static double asDouble(Object o) {
        if (o == null) return 0d;
        if (o instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0d; }
    }

    private static LocalDate asDate(Object o) {
        if (o instanceof LocalDate d) return d;
        if (o == null) return null;
        try { return LocalDate.parse(String.valueOf(o)); } catch (Exception e) { return null; }
    }

   private void fillForm(RoomRow r) {
    txtRoomNum.setText(r.roomNum());
    txtRate.setText(String.valueOf(r.price()));
    txtCapacity.setText(r.capacity());
    cbStatus.setValue(r.statusText());
    cbType.setValue(r.type());
    
    txFacilities.clear();
}
    @FXML
    private void onFilter() {
        onSearch();
    }

    @FXML
    private void onSearch() {
        String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        Integer cap = null;
        if (txtCapacity.getText() != null && !txtCapacity.getText().isBlank()) {
            try { cap = Integer.parseInt(txtCapacity.getText().trim()); } catch (Exception ignored) {}
        }
        String type = cbType.getValue();
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();

        if (q.isEmpty() && cap == null && type == null && from == null && to == null) {
            reloadAll();
            return;
        }

        rows.setAll(mapToRows(RoomRepository.searchRooms(cap, type, from, to)));
    }

    @FXML
    private void onSave() {
        String roomNum = s(txtRoomNum.getText()).trim();
        String type = cbType.getValue();
        String statusL = cbStatus.getValue();
        double rate;
        try { rate = Double.parseDouble(txtRate.getText().trim()); }
        catch (Exception e){ alert("Rate invalid"); return; }
        if (roomNum.isEmpty() || type == null || statusL == null) { alert("Fill required fields."); return; }

        int status = switch (statusL) {
            case "Empty" -> 0;
            case "Occupied" -> 1;
            case "Cleaning" -> 2;
            default -> 0;
        };

        try {
            boolean ok = false;
            try {
                var m = RoomRepository.class.getMethod("updateRoom", String.class, String.class, double.class, int.class);
                Object ret = m.invoke(null, roomNum, type, rate, status);
                ok = !(ret instanceof Boolean) || (Boolean) ret; // accept void or true
            } catch (NoSuchMethodException nsme) {
                ok = RoomRepository.updateStatusByRoomNumber(roomNum, status);
            }
            if (!ok) { alert("Save failed."); return; }
        } catch (Throwable t) {
            alert("Error: " + t.getMessage());
            return;
        }

        reloadAll();
        alert("Saved");
    }

    private static String s(Object o){ return o==null? "" : String.valueOf(o); }

    private void alert(String m){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    // Simple row model for the TableView
    public record RoomRow(String roomNum, String type, String capacity, String statusText,
                          double price, LocalDate nearIn, LocalDate nearOut) {}

}
