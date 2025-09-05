package HotelApp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RoomTypeAmenity {

    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty quality = new SimpleStringProperty();
    private final IntegerProperty capacity = new SimpleIntegerProperty();
    private final StringProperty amenities = new SimpleStringProperty();

    public RoomTypeAmenity(String category, String quality, int capacity, String amenities) {
        this.category.set(category);
        this.quality.set(quality);
        this.capacity.set(capacity);
        this.amenities.set(amenities);
    }

    // Properties
    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty qualityProperty() {
        return quality;
    }

    public IntegerProperty capacityProperty() {
        return capacity;
    }

    public StringProperty amenitiesProperty() {
        return amenities;
    }

    // Getters
    public String getCategory() {
        return category.get();
    }

    public String getQuality() {
        return quality.get();
    }

    public int getCapacity() {
        return capacity.get();
    }

    public String getAmenities() {
        return amenities.get();
    }
}
