package HotelApp.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Booking {
    private final StringProperty bookingId   = new SimpleStringProperty();
    private final StringProperty guestName   = new SimpleStringProperty();
    private final StringProperty roomNumber  = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> checkInDate  = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();
    private final IntegerProperty status = new SimpleIntegerProperty(2);   // 2 = Booking active
    private final DoubleProperty price  = new SimpleDoubleProperty(0.0);

    public Booking(String id, String guest, String room, LocalDate ci, LocalDate co, int status) {
        this.bookingId.set(id);
        this.guestName.set(guest);
        this.roomNumber.set(room);
        this.checkInDate.set(ci);
        this.checkOutDate.set(co);
        this.status.set(status);
    }

    public Booking(String guest, String room, LocalDate ci, LocalDate co, Object unused) {
        this(null, guest, room, ci, co, 2);
    }

    // properties
    public StringProperty bookingIdProperty() { return bookingId; }
    public StringProperty guestNameProperty() { return guestName; }
    public StringProperty roomNumberProperty() { return roomNumber; }
    public ObjectProperty<LocalDate> checkInDateProperty() { return checkInDate; }
    public ObjectProperty<LocalDate> checkOutDateProperty() { return checkOutDate; }
    public IntegerProperty statusProperty() { return status; }
    public DoubleProperty priceProperty() { return price; }

    // getters
    public String getBookingId() { return bookingId.get(); }
    public String getGuestName() { return guestName.get(); }
    public String getRoomNumber() { return roomNumber.get(); }
    public LocalDate getCheckInDate() { return checkInDate.get(); }
    public LocalDate getCheckOutDate() { return checkOutDate.get(); }
    public int getStatus() { return status.get(); }
    public double getPrice() { return price.get(); }

    // setters
    public void setBookingId(String id) { bookingId.set(id); }
    public void setGuestName(String g) { guestName.set(g); }
    public void setRoomNumber(String r) { roomNumber.set(r); }
    public void setCheckInDate(LocalDate d) { checkInDate.set(d); }
    public void setCheckOutDate(LocalDate d) { checkOutDate.set(d); }
    public void setStatus(int s) { status.set(s); }
    public void setPrice(double v) { price.set(v); }
}
