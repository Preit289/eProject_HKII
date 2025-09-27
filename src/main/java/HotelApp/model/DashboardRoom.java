package HotelApp.model;

/**
 * Simple DTO for dashboard rows. Use plain String getters so the class does not expose JavaFX module
 * types in its public API (avoids module compile issues).
 */
public class DashboardRoom {

    private final String roomNumber;
    private final String status;
    private final String guestNames;

    public DashboardRoom(String roomNumber, String status, String guestNames) {
        this.roomNumber = roomNumber;
        this.status = status;
        this.guestNames = guestNames == null ? "" : guestNames;
    }

    public String getRoomNumber() { return roomNumber; }
    public String getStatus() { return status; }
    public String getGuestNames() { return guestNames; }
}
