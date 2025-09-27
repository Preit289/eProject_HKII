package HotelApp;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import HotelApp.model.Customer;
import HotelApp.repository.CustomerRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@SuppressWarnings("unused")
public class CustomerController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, String> idColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, LocalDate> dobColumn;
    @FXML
    private TableColumn<Customer, String> identityColumn;
    @FXML
    private TableColumn<Customer, Boolean> foreignerColumn;
    @FXML
    private TableColumn<Customer, Boolean> genderColumn;
    @FXML
    private TableColumn<Customer, Boolean> childColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;

    @FXML
    private TextField customerNameField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField identityNumberField;
    @FXML
    private CheckBox foreignerCheckBox;
    @FXML
    private CheckBox genderCheckBox;
    @FXML
    private CheckBox childCheckBox;
    @FXML
    private TextField phoneNumberField;

    private CustomerRepository customerRepository;
    private ObservableList<Customer> customerList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerRepository = new CustomerRepository();
        customerList = FXCollections.observableArrayList();

        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        identityColumn.setCellValueFactory(new PropertyValueFactory<>("citizenIdentityNumber"));
    // boolean columns: use readable string (Yes/No) via cell value factory
        foreignerColumn.setCellValueFactory(cell -> cell.getValue().isForeignerProperty());
        foreignerColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Customer, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Có" : "Không");
                }
            }
        });

        genderColumn.setCellValueFactory(cell -> cell.getValue().genderProperty());
        genderColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Customer, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Nam" : "Nữ");
                }
            }
        });

        childColumn.setCellValueFactory(cell -> cell.getValue().isChildProperty());
        childColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Customer, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Có" : "Không");
                }
            }
        });
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        

        // Load data
        loadCustomers();

        // Add selection listener
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                customerNameField.setText(newSelection.getCustomerName());
                dobPicker.setValue(newSelection.getDob());
                identityNumberField.setText(newSelection.getCitizenIdentityNumber());
                foreignerCheckBox.setSelected(newSelection.isIsForeigner());
                genderCheckBox.setSelected(newSelection.isGender());
                childCheckBox.setSelected(newSelection.isIsChild());
                phoneNumberField.setText(newSelection.getPhoneNum());
            }
        });
        
    }

    @FXML
    private void handleAddCustomer(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        Customer newCustomer = new Customer(
                customerRepository.getNextCustomerId(),
                customerNameField.getText(),
                dobPicker.getValue(),
                identityNumberField.getText(),
                foreignerCheckBox.isSelected(),
                genderCheckBox.isSelected(),
                childCheckBox.isSelected(),
                phoneNumberField.getText()
        );

        if (customerRepository.addCustomer(newCustomer)) {
            customerList.add(newCustomer);
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add customer!");
        }
    }

    @FXML
    private void handleUpdateCustomer(ActionEvent event) {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to update!");
            return;
        }

        if (!validateInput()) {
            return;
        }

        selectedCustomer.setCustomerName(customerNameField.getText());
        selectedCustomer.setDob(dobPicker.getValue());
        selectedCustomer.setCitizenIdentityNumber(identityNumberField.getText());
        selectedCustomer.setIsForeigner(foreignerCheckBox.isSelected());
        selectedCustomer.setGender(genderCheckBox.isSelected());
        selectedCustomer.setIsChild(childCheckBox.isSelected());
        selectedCustomer.setPhoneNum(phoneNumberField.getText());

        if (customerRepository.updateCustomer(selectedCustomer)) {
            customerTable.refresh();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update customer!");
        }
    }

    @FXML
    private void handleDeleteCustomer(ActionEvent event) {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to delete!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this customer?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (customerRepository.deleteCustomer(selectedCustomer.getCustomerId())) {
                customerList.remove(selectedCustomer);
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete customer!");
            }
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
        customerTable.getSelectionModel().clearSelection();
    }

    private void loadCustomers() {
        customerList.clear();
        customerList.addAll(customerRepository.getAllCustomers());
        customerTable.setItems(customerList);
    }

    private void clearFields() {
        customerNameField.clear();
        dobPicker.setValue(null);
        identityNumberField.clear();
        foreignerCheckBox.setSelected(false);
        genderCheckBox.setSelected(false);
        childCheckBox.setSelected(false);
        phoneNumberField.clear();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (customerNameField.getText().trim().isEmpty()) {
            errorMessage.append("Customer name is required!\n");
        }

        if (!phoneNumberField.getText().trim().isEmpty()
                && !phoneNumberField.getText().matches("\\d{10,11}")) {
            errorMessage.append("Phone number must be 10-11 digits!\n");
        }

        if (!identityNumberField.getText().trim().isEmpty()
                && !identityNumberField.getText().matches("\\d{9,12}")) {
            errorMessage.append("Identity number must be 9-12 digits!\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
