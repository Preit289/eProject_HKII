package HotelApp;

import HotelApp.repository.PricingRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.*;

public class PricingController {

    @FXML
    private ChoiceBox<String> cbRoomType;
    @FXML
    private TextField txtPrice;
    @FXML
    private TableView<RoomPrice> tblPrices;
    @FXML
    private TableColumn<RoomPrice, String> colRoomType;
    @FXML
    private TableColumn<RoomPrice, Double> colPrice;

    private final ObservableList<RoomPrice> prices = FXCollections.observableArrayList();
    private final ObservableList<String> roomTypes = FXCollections.observableArrayList(
            "Single", "Double", "Suite", "Deluxe"
    );

    @FXML
    private void initialize() {
        cbRoomType.setItems(roomTypes);
        colRoomType.setCellValueFactory(data -> data.getValue().roomTypeProperty());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());

        // Load prices from repository
        prices.addAll(PricingRepository.getAll());
        tblPrices.setItems(prices);
    }

    @FXML
    private void onSave() {
        String roomType = cbRoomType.getValue();
        String priceText = txtPrice.getText();

        if (roomType == null || priceText.isBlank()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                showAlert("Price cannot be negative.");
                return;
            }

            RoomPrice roomPrice = new RoomPrice(roomType, price);
            prices.removeIf(p -> p.getRoomType().equals(roomType));
            prices.add(roomPrice);
            PricingRepository.save(roomPrice); // Save to repository
            clearForm();
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid price.");
        }
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private void clearForm() {
        cbRoomType.setValue(null);
        txtPrice.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static class RoomPrice {

        private final StringProperty roomType = new SimpleStringProperty();
        private final DoubleProperty price = new SimpleDoubleProperty();

        public RoomPrice(String roomType, double price) {
            this.roomType.set(roomType);
            this.price.set(price);
        }

        public StringProperty roomTypeProperty() {
            return roomType;
        }

        public DoubleProperty priceProperty() {
            return price;
        }

        public String getRoomType() {
            return roomType.get();
        }

        public double getPrice() {
            return price.get();
        }

        public void setPrice(double price) {
            this.price.set(price);
        }
    }
}
