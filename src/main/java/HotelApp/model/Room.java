package HotelApp.model;

import javafx.beans.property.*;

public class Room {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty quality = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();

    // Constructor đầy đủ
    public Room(String id, String category, String roomNumber,
                String quality, double price, String status) {
        this.id.set(id);
        this.category.set(category);
        this.roomNumber.set(roomNumber);
        this.quality.set(quality);
        this.price.set(price);
        this.status.set(status);
    }

    // --- property methods (để TableView bind được) ---
    public StringProperty idProperty() { return id; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty roomNumberProperty() { return roomNumber; }
    public StringProperty qualityProperty() { return quality; }
    public DoubleProperty priceProperty() { return price; }
    public StringProperty statusProperty() { return status; }

    // --- getters ---
    public String getId() { return id.get(); }
    public String getCategory() { return category.get(); }
    public String getRoomNumber() { return roomNumber.get(); }
    public String getQuality() { return quality.get(); }
    public double getPrice() { return price.get(); }
    public String getStatus() { return status.get(); }

    // --- setters ---
    public void setId(String id) { this.id.set(id); }
    public void setCategory(String category) { this.category.set(category); }
    public void setRoomNumber(String roomNumber) { this.roomNumber.set(roomNumber); }
    public void setQuality(String quality) { this.quality.set(quality); }
    public void setPrice(double price) { this.price.set(price); }
    public void setStatus(String status) { this.status.set(status); }
}
