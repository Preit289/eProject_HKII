package HotelApp.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Checkin {
    private final StringProperty guestName;
    private final StringProperty guestPhone;
    private final StringProperty roomNumbers;
    private final StringProperty roomCategory;
    private final StringProperty roomType;
    private final StringProperty plannedCheckIn;
    private final StringProperty plannedCheckOut;

    public Checkin(String guestName, String guestPhone, String roomNumbers,
                   String roomCategory, String roomType,
                   String plannedCheckIn, String plannedCheckOut) {
        this.guestName = new SimpleStringProperty(guestName);
        this.guestPhone = new SimpleStringProperty(guestPhone);
        this.roomNumbers = new SimpleStringProperty(roomNumbers);
        this.roomCategory = new SimpleStringProperty(roomCategory);
        this.roomType = new SimpleStringProperty(roomType);
        this.plannedCheckIn = new SimpleStringProperty(plannedCheckIn);
        this.plannedCheckOut = new SimpleStringProperty(plannedCheckOut);
    }

    // Getters (for TableView binding)
    public String getGuestName() { return guestName.get(); }
    public String getGuestPhone() { return guestPhone.get(); }
    public String getRoomNumbers() { return roomNumbers.get(); }
    public String getRoomCategory() { return roomCategory.get(); }
    public String getRoomType() { return roomType.get(); }
    public String getPlannedCheckIn() { return plannedCheckIn.get(); }
    public String getPlannedCheckOut() { return plannedCheckOut.get(); }

    // Property methods (optional but recommended for binding)
    public StringProperty guestNameProperty() { return guestName; }
    public StringProperty guestPhoneProperty() { return guestPhone; }
    public StringProperty roomNumbersProperty() { return roomNumbers; }
    public StringProperty roomCategoryProperty() { return roomCategory; }
    public StringProperty roomTypeProperty() { return roomType; }
    public StringProperty plannedCheckInProperty() { return plannedCheckIn; }
    public StringProperty plannedCheckOutProperty() { return plannedCheckOut; }
}
