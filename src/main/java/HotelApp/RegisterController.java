package HotelApp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import HotelApp.repository.AccountRepository;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox adminCheckBox;
    @FXML private Label errorLabel;

    @FXML
    private void onRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();
        boolean isAdmin = adminCheckBox.isSelected();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        if (AccountRepository.register(username, password, isAdmin)) {
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Successfully registered!");
            try {
                App.setRoot("Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Failed to register (username may exist).");
        }
    }

    @FXML
    private void onBackToLogin() throws IOException {
        App.setRoot("Login");
    }
}
