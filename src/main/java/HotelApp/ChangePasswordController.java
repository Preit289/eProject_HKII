package HotelApp;

import HotelApp.repository.AccountRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ChangePasswordController {
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    private void onChangePassword() {
        String newPw = txtNewPassword.getText().trim();
        String confirm = txtConfirmPassword.getText().trim();

        if (newPw.isEmpty() || newPw.length() < 3) {
            showAlert("Error", "Password must be at least 3 characters.");
            return;
        }
        if (!newPw.equals(confirm)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        if (AccountRepository.updatePassword(username, newPw)) {
            showAlert("Success", "Password changed successfully. Please log in again.");
            Stage stage = (Stage) txtNewPassword.getScene().getWindow();
            App.showLogin(stage); 
        } else {
            showAlert("Error", "Failed to change password.");
        }

    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
