package HotelApp.model;

import java.time.LocalDate;


public class Booking {
    private String bookingId;
    private String guestName;     
    private String roomNumber;    
    private LocalDate checkInDate;   
    private LocalDate checkOutDate;  
    private int status;             
    private double price;            

    public Booking() {}

    public Booking(String id, String guest, String room,
                   LocalDate ci, LocalDate co, int status) {
        this.bookingId = id;
        this.guestName = guest;
        this.roomNumber = room;
        this.checkInDate = ci;
        this.checkOutDate = co;
        this.status = status;
    }

    // getters/setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
