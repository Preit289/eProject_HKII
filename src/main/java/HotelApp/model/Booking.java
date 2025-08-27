package HotelApp.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Booking {

    private final StringProperty guestName = new SimpleStringProperty();
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> checkInDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty("Booked");
    private final ObjectProperty<Room> room = new SimpleObjectProperty<>();
    private final DoubleProperty price = new SimpleDoubleProperty(0.0);

    public Booking(String guest, String roomNumber, LocalDate checkIn, LocalDate checkOut, Room room) {
        this.guestName.set(guest);
        this.roomNumber.set(roomNumber);
        this.checkInDate.set(checkIn);
        this.checkOutDate.set(checkOut);
        this.room.set(room);
    }

    public StringProperty guestNameProperty() {
        return guestName;
    }

    public StringProperty roomNumberProperty() {
        return roomNumber;
    }

    public ObjectProperty<LocalDate> checkInDateProperty() {
        return checkInDate;
    }

    public ObjectProperty<LocalDate> checkOutDateProperty() {
        return checkOutDate;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public ObjectProperty<Room> roomProperty() {
        return room;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public String getGuestName() {
        return guestName.get();
    }

    public String getRoomNumber() {
        return roomNumber.get();
    }

    public LocalDate getCheckInDate() {
        return checkInDate.get();
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate.get();
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public Room getRoom() {
        return room.get();
    }

    public void setRoom(Room room) {
        this.room.set(room);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }
}
