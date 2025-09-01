package HotelApp;

import HotelApp.model.Services;
import HotelApp.repository.ServicesRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class ServicesController {

    @FXML
    private TextField txtServiceName;
    @FXML
    private TextField txtServicePrice;
    @FXML
    private TextArea txtServiceDescription;
    @FXML
    private TableView<Services> tblServices;
    @FXML
    private TableColumn<Services, String> colServiceId;
    @FXML
    private TableColumn<Services, String> colServiceName;
    @FXML
    private TableColumn<Services, Number> colServicePrice;
    @FXML
    private TableColumn<Services, String> colServiceDescription;

    private final ServicesRepository repository = new ServicesRepository();
    private final ObservableList<Services> servicesList = FXCollections.observableArrayList();
    private Services selectedService;

    @FXML
    private void initialize() {
        // Initialize table columns
        colServiceId.setCellValueFactory(data -> data.getValue().serviceIdProperty());
        colServiceName.setCellValueFactory(data -> data.getValue().serviceNameProperty());
        colServicePrice.setCellValueFactory(data -> data.getValue().servicePriceProperty());
        colServiceDescription.setCellValueFactory(data -> data.getValue().serviceDescriptionProperty());

        // Load services from database
        loadServices();

        // Add click listener to table
        tblServices.setOnMouseClicked(this::onTableClicked);
    }

    private void loadServices() {
        servicesList.clear();
        servicesList.addAll(repository.getAllServices());
        tblServices.setItems(servicesList);
    }

    @FXML
    private void onAdd() {
        if (!validateInput()) {
            return;
        }

        String serviceId = repository.getNextServiceId();
        Services newService = new Services(
                serviceId,
                txtServiceName.getText(),
                Integer.parseInt(txtServicePrice.getText()),
                txtServiceDescription.getText()
        );

        if (repository.addService(newService)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Service added successfully!");
            loadServices();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add service!");
        }
    }

    @FXML
    private void onUpdate() {
        if (selectedService == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a service to update!");
            return;
        }

        if (!validateInput()) {
            return;
        }

        selectedService.setServiceName(txtServiceName.getText());
        selectedService.setServicePrice(Integer.parseInt(txtServicePrice.getText()));
        selectedService.setServiceDescription(txtServiceDescription.getText());

        if (repository.updateService(selectedService)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Service updated successfully!");
            loadServices();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update service!");
        }
    }

    @FXML
    private void onDelete() {
        if (selectedService == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a service to delete!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this service?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait();

        if (confirmation.getResult() == ButtonType.YES) {
            if (repository.deleteService(selectedService.getServiceId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Service deleted successfully!");
                loadServices();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete service!");
            }
        }
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private void onTableClicked(MouseEvent event) {
        if (event.getClickCount() == 1) {
            selectedService = tblServices.getSelectionModel().getSelectedItem();
            if (selectedService != null) {
                txtServiceName.setText(selectedService.getServiceName());
                txtServicePrice.setText(String.valueOf(selectedService.getServicePrice()));
                txtServiceDescription.setText(selectedService.getServiceDescription());
            }
        }
    }

    private boolean validateInput() {
        String name = txtServiceName.getText();
        String price = txtServicePrice.getText();

        if (name.isEmpty() || price.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill in all required fields!");
            return false;
        }

        try {
            int priceValue = Integer.parseInt(price);
            if (priceValue < 0) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Price cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid price!");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtServiceName.clear();
        txtServicePrice.clear();
        txtServiceDescription.clear();
        selectedService = null;
        tblServices.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
