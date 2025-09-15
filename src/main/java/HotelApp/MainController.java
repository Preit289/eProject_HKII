package HotelApp;

import java.io.IOException;

import HotelApp.repository.AccountRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Label pageTitle;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;
    @FXML
    private StackPane contentArea;

    //login role//////////////////////////
    @FXML private Button btnRoomManagement;
    @FXML private Button btnAdminDashboard;

    private boolean isAdmin;

    public void setRole(boolean isAdmin) {
        this.isAdmin = isAdmin;

        btnRoomManagement.setVisible(isAdmin);
        btnAdminDashboard.setVisible(isAdmin);
    }

    @FXML
    private void onLogout() throws IOException {
        try {
            AccountRepository.logout();

            // load Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loader.load();

            // get stage from current scene
            Stage stage = (Stage) pageTitle.getScene().getWindow();

            // create new scene for login
            Scene scene = new Scene(loginRoot);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Login - Hotel Management");
            stage.show();
            statusLabel.setText("Logged out successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error logging out.");
        }
    }

    ///////////////////////////////////////
    
    @FXML
    private void initialize() {
        showDashboard();
    }

    @FXML
    private void onSearch() {
        statusLabel.setText("Searching: " + searchField.getText());
    }

    private void loadView(String fxml, String title) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(view);
            pageTitle.setText(title);
            statusLabel.setText("Viewing: " + title);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading " + title);
        }
    }

    @FXML
    private void showDashboard() {
        loadView("Dashboard.fxml", "Dashboard");
    }

    @FXML
    private void showBooking() {
        loadView("/HotelApp/Booking.fxml", "Booking");
    }

    @FXML
    private void showCheckin() {
        loadView("Checkin.fxml", "Check-in");
    }

    @FXML
    private void showCustomers() {
        loadView("Customer.fxml", "Customers Management");
    }

    @FXML
    private void showServices() {
        loadView("Services.fxml", "Services Management");
    }

    @FXML
    private void showRoomManagement() {
        loadView("RoomMgmt.fxml", "Room Management");
    }

    @FXML
    private void showAdminDashboard() {
        loadView("AdminDashboard.fxml", "Admin Dashboard");
    }
}
