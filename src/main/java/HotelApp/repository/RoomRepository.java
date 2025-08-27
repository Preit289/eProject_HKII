package HotelApp.repository;

import HotelApp.model.Room;
import java.util.ArrayList;
import java.util.List;

public class RoomRepository {

    private static final List<Room> rooms = new ArrayList<>();

    public static List<Room> getAll() {
        return new ArrayList<>(rooms);
    }

    public static Room findByRoomNumber(String roomNumber) {
        return rooms.stream()
                .filter(r -> r.getRoomNumber().equals(roomNumber))
                .findFirst()
                .orElse(null);
    }

    public static void save(Room room) {
        rooms.add(room);
    }
}
