package HotelApp;

import HotelApp.model.Account;
import HotelApp.repository.AccountRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

   @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        try {
            System.out.println("Attempting login for user: " + username);

            boolean success = AccountRepository.login(username, password);

            if (success) {
                Account user = AccountRepository.getCurrentUser();
                System.out.println("Login success. User: " + user.getUsername() + ", isAdmin: " + user.isAdmin());

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                Parent root = loader.load();

                MainController mainCtrl = loader.getController();
                mainCtrl.setRole(user.isAdmin());

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                System.err.println("Login failed for user: " + username);
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Invalid username or password.");
                usernameField.clear();
                passwordField.clear();
            }

        } catch (IOException e) {
            System.err.println("FXML loading failed: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Error loading main screen.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Unexpected error occurred.");
        }
    }
}
