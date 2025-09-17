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
    @FXML
    private TableView<Account> accountTable;
    @FXML
    private TableColumn<Account, String> colUsername;
    @FXML
    private TableColumn<Account, String> colPassword;
    @FXML
    private TableColumn<Account, String> colIsAdmin;

    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private ChoiceBox<String> cbRole;

    private ObservableList<Account> accounts;

    @FXML
    private Button btnAdd, btnUpdate, btnDelete, btnClear, btnBack;

    @FXML
    public void initialize() {

        // Username
        colUsername.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        // password
        colPassword.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPassword()));

        // Role
        colIsAdmin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isAdmin() ? "Admin" : "Staff"));

        // Default role
        cbRole.setItems(FXCollections.observableArrayList("Admin", "Staff"));
        cbRole.setValue("Staff");

        // Load
        try {
            var list = AccountRepository.getAllAccounts();
            accounts = FXCollections.observableArrayList(list != null ? list : FXCollections.observableArrayList());
        } catch (Exception ex) {
            accounts = FXCollections.observableArrayList();
            showAlert("Error", "Failed to load accounts.\n" + ex.getMessage());
        }
        accountTable.setItems(accounts);

        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtUsername.setText(newSel.getUsername());
                txtPassword.setText(newSel.getPassword());
                cbRole.setValue(newSel.isAdmin() ? "Admin" : "Staff");
                txtUsername.setDisable(true);
            }
        });

        btnUpdate.disableProperty().bind(accountTable.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(accountTable.getSelectionModel().selectedItemProperty().isNull());
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
        if (username.length() < 3) {
            showAlert("Error", "Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 3) {
            showAlert("Error", "Password must be at least 3 characters.");
            return;
        }

        Account newAcc = new Account(username, password, "Admin".equals(role));
        if (AccountRepository.addAccount(newAcc)) {
            accounts.add(newAcc); // TableView hiển thị mask
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
        if (password.isEmpty() || role == null) {
            showAlert("Error", "Please fill all fields.");
            return;
        }
        if (password.length() < 3) {
            showAlert("Error", "Password must be at least 3 characters.");
            return;
        }

        boolean isAdmin = "Admin".equals(role);
        if (AccountRepository.updateAccount(selected.getUsername(), password, isAdmin)) {
            selected.setPassword(password);
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
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user: " + selected.getUsername() + "?",
                    ButtonType.OK, ButtonType.CANCEL);
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
        cbRole.setValue("Staff");
        accountTable.getSelectionModel().clearSelection();
        txtUsername.setDisable(false);
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
