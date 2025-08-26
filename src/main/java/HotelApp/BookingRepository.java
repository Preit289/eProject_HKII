package HotelApp;

import java.util.ArrayList;
import java.util.List;

public class BookingRepository {

    private static final List<Booking> bookings = new ArrayList<>();

    public static List<Booking> getAll() {
        return new ArrayList<>(bookings);
    }

    public static void save(Booking booking) {
        bookings.add(booking);
    }
}
