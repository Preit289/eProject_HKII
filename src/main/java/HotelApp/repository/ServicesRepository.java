package HotelApp.repository;

import HotelApp.PricingController;
import java.util.ArrayList;
import java.util.List;

public class PricingRepository {

    private static final List<PricingController.RoomPrice> prices = new ArrayList<>();

    public static List<PricingController.RoomPrice> getAll() {
        return new ArrayList<>(prices);
    }

    public static double getPriceForRoomType(String roomType) {
        return prices.stream()
                .filter(p -> p.getRoomType().equals(roomType))
                .findFirst()
                .map(PricingController.RoomPrice::getPrice)
                .orElse(0.0); // Default to 0 if not found
    }

    public static void save(PricingController.RoomPrice price) {
        prices.removeIf(p -> p.getRoomType().equals(price.getRoomType()));
        prices.add(price);
    }
}
