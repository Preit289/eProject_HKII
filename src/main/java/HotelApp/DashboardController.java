package HotelApp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

import HotelApp.repository.CheckinRepository;
import HotelApp.repository.RoomRepository;
import HotelApp.model.DashboardRoom;

public class DashboardController {

    @FXML
    private Label lblCheckins;
    @FXML
    private Label lblCheckouts;
    @FXML
    private TableView<DashboardRoom> tableStats;

    @FXML
    private void initialize() {
        try {
            int checkins = CheckinRepository.countCurrentCheckins();
            int checkouts = CheckinRepository.countTodayCheckouts();
            lblCheckins.setText(String.valueOf(checkins));
            lblCheckouts.setText(String.valueOf(checkouts));

            // Configure table columns to use DashboardRoom DTO (room, status, guest names)
            // Replace TableView's generic type at runtime by populating with DashboardRoom objects.
            if (tableStats.getColumns().size() >= 3) {
                @SuppressWarnings("unchecked")
                TableColumn<DashboardRoom, String> colRoom = (TableColumn<DashboardRoom, String>) tableStats.getColumns().get(0);
                @SuppressWarnings("unchecked")
                TableColumn<DashboardRoom, String> colStatus = (TableColumn<DashboardRoom, String>) tableStats.getColumns().get(1);
                @SuppressWarnings("unchecked")
                TableColumn<DashboardRoom, String> colGuest = (TableColumn<DashboardRoom, String>) tableStats.getColumns().get(2);

                colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
                colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
                colGuest.setCellValueFactory(new PropertyValueFactory<>("guestNames"));
            }

            // Load dashboard rows (room + aggregated guest names for checked-in stayings)
            tableStats.setItems(FXCollections.observableArrayList(RoomRepository.getDashboardRooms()));

        } catch (Throwable t) {
            // Fall back to defaults if repository methods or DB not available
            lblCheckins.setText("0");
            lblCheckouts.setText("0");
            tableStats.setItems(FXCollections.observableArrayList());
        }
    }
}
