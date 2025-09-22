package HotelApp;

import java.io.IOException;

import HotelApp.model.Account;
import HotelApp.repository.AccountRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        try {
            if (username.isEmpty() || password.isEmpty()) {
                throw new Exception("Please enter username and password.");
            }

            boolean success = AccountRepository.login(username, password);
            if (success) {
                Account user = AccountRepository.getCurrentUser();

                if ("123456".equals(user.getPassword())) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
                    Parent root = loader.load();

                    ChangePasswordController controller = loader.getController();
                    controller.setUsername(username);

                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Change Password - Hotel Management");
                    stage.show();
                    return;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                Parent root = loader.load();

                MainController controller = loader.getController();
                controller.setRole(user.isAdmin());

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Hotel Management - Main");
                stage.show();

            } else {
                throw new Exception("Invalid username or password.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // console
            showErrorDialog(e.getMessage()); // popup Alert
        }
    }

    private void showErrorDialog(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadChangePasswordView(String username) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
        Parent root = loader.load();

        ChangePasswordController controller = loader.getController();
        controller.setUsername(username);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Change Password - Hotel Management");
        stage.show();
    }

    private void loadMainView(boolean isAdmin) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setRole(isAdmin);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Hotel Management - Main");
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
