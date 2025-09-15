package HotelApp;

import HotelApp.model.Account;
import HotelApp.repository.AccountRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class AdminDashboardController {
    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> colUsername;
    @FXML private TableColumn<Account, String> colPassword;
    @FXML private TableColumn<Account, String> colIsAdmin;

    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private ChoiceBox<String> cbRole;

    private ObservableList<Account> accounts;

    @FXML
    public void initialize() {
        colUsername.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        colPassword.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPassword()));
        colIsAdmin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isAdmin() ? "Admin" : "Staff"));

        accounts = FXCollections.observableArrayList(AccountRepository.getAllAccounts());
        accountTable.setItems(accounts);

        cbRole.setItems(FXCollections.observableArrayList("Admin", "Staff"));

        // Khi chọn 1 account trong bảng -> load vào form
        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtUsername.setText(newSel.getUsername());
                txtPassword.setText(newSel.getUsername());
                cbRole.setValue(newSel.isAdmin() ? "Admin" : "Staff");
            }
        });
    }

    @FXML
    private void onAdd() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = cbRole.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Please fill all fields.");
            return;
        }

        Account newAcc = new Account(username, password, "Admin".equals(role));
        if (AccountRepository.addAccount(newAcc)) {
            accounts.add(newAcc);
            showAlert("Success", "Account added successfully.");
            onClear();
        } else {
            showAlert("Error", "Failed to add account. Username may exist.");
        }
    }

    @FXML
    private void onUpdate() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select an account to update.");
            return;
        }

        String password = txtPassword.getText().trim();
        String role = cbRole.getValue();
        boolean isAdmin = "Admin".equals(role);

        if (AccountRepository.updateAccount(selected.getUsername(), password, isAdmin)) {
            selected.setAdmin(isAdmin);
            accountTable.refresh();
            showAlert("Success", "Account updated successfully.");
            onClear();
        } else {
            showAlert("Error", "Failed to update account.");
        }
    }

    @FXML
    private void onDelete() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user: " + selected.getUsername() + "?", ButtonType.OK, ButtonType.CANCEL);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (AccountRepository.deleteAccount(selected.getUsername())) {
                        accounts.remove(selected);
                        showAlert("Success", "Account deleted successfully.");
                    } else {
                        showAlert("Error", "Failed to delete account.");
                    }
                }
            });
        }
    }

    @FXML
    private void onClear() {
        txtUsername.clear();
        txtPassword.clear();
        cbRole.setValue(null);
        accountTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void onBackToLogin() throws IOException {
        App.setRoot("Login");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
