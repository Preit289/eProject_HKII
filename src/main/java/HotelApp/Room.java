package HotelApp;

import javafx.beans.property.*;

public class Room {

    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Room(String roomNumber, String type, String status) {
        this.roomNumber.set(roomNumber);
        this.type.set(type);
        this.status.set(status);
    }

    public StringProperty roomNumberProperty() {
        return roomNumber;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getRoomNumber() {
        return roomNumber.get();
    }

    public String getType() {
        return type.get();
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
