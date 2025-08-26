package HotelApp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class DashboardController {

    @FXML
    private Label lblCheckins;
    @FXML
    private Label lblCheckouts;
    @FXML
    private TableView<?> tableStats;

    @FXML
    private void initialize() {
        // Demo data
        lblCheckins.setText("5");
        lblCheckouts.setText("3");
    }
}
