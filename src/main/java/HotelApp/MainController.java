package HotelApp;

import HotelApp.repository.AccountRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Label pageTitle;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;
    @FXML
    private StackPane contentArea;

    // login role
    @FXML
    private Button btnRoomManagement;
    @FXML
    private Button btnAdminDashboard;

    private boolean isAdmin;

    public void setRole(boolean isAdmin) {
        this.isAdmin = isAdmin;
        btnRoomManagement.setVisible(isAdmin);
        btnAdminDashboard.setVisible(isAdmin);
    }

    @FXML
    private void onLogout() {
        try {
            AccountRepository.logout();
            Stage stage = (Stage) pageTitle.getScene().getWindow();
            App.showLogin(stage);
            statusLabel.setText("Logged out successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error logging out.");
        }
    }

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
