package HotelApp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import HotelApp.repository.AccountRepository;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void onLogin() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        if (AccountRepository.login(username, password)) {
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Login successful!");
            App.setRoot("Main");
        } else {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Invalid username or password.");
            passwordField.clear();
        }
    }

    @FXML
    private void onRegister() throws IOException {
        App.setRoot("Register");
    }
}
