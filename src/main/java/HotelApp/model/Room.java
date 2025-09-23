package HotelApp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {

    private final StringProperty roomId = new SimpleStringProperty();
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty quality = new SimpleStringProperty();
    private final IntegerProperty price = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty amenities = new SimpleStringProperty();
    private final IntegerProperty capacity = new SimpleIntegerProperty();

    public Room(String roomId, String roomNumber, String category, String quality,
            int price, String status, String amenities, int capacity) {
        this.roomId.set(roomId);
        this.roomNumber.set(roomNumber);
        this.category.set(category);
        this.quality.set(quality);
        this.price.set(price);
        this.status.set(status);
        this.amenities.set(amenities);
        this.capacity.set(capacity);
    }

    // Properties
    public StringProperty roomIdProperty() {
        return roomId;
    }

    public StringProperty roomNumberProperty() {
        return roomNumber;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty qualityProperty() {
        return quality;
    }

    public IntegerProperty priceProperty() {
        return price;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty amenitiesProperty() {
        return amenities;
    }

    public IntegerProperty capacityProperty() {
        return capacity;
    }

    // Setters
    public void setStatus(String status) {
        this.status.set(status);
    }

    // Getters
    public String getRoomId() {
        return roomId.get();
    }

    public String getRoomNumber() {
        return roomNumber.get();
    }

    public String getCategory() {
        return category.get();
    }

    public String getQuality() {
        return quality.get();
    }

    public int getPrice() {
        return price.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getAmenities() {
        return amenities.get();
    }

    public int getCapacity() {
        return capacity.get();
    }
}
