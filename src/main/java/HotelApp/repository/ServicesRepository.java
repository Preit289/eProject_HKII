package HotelApp.repository;

import HotelApp.ServicesController;
import java.util.ArrayList;
import java.util.List;

public class ServicesRepository {

    private static final List<ServicesController.RoomPrice> prices = new ArrayList<>();

    public static List<ServicesController.RoomPrice> getAll() {
        return new ArrayList<>(prices);
    }

    public static double getPriceForRoomType(String roomType) {
        return prices.stream()
                .filter(p -> p.getRoomType().equals(roomType))
                .findFirst()
                .map(ServicesController.RoomPrice::getPrice)
                .orElse(0.0); // Default to 0 if not found
    }

    public static void save(ServicesController.RoomPrice price) {
        prices.removeIf(p -> p.getRoomType().equals(price.getRoomType()));
        prices.add(price);
    }
}
