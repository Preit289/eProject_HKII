# Hotel Management System (FXML)

Ứng dụng quản lý khách sạn viết bằng JavaFX (FXML) dùng để quản lý đặt phòng, check-in, check-out, quản lý phòng và định giá phòng.

## Tính năng chính
- Quản lý booking: tạo và lưu booking — xem mã nguồn ở [`HotelApp.BookingController`](src/main/java/HotelApp/BookingController.java) và giao diện [`src/main/resources/HotelApp/Booking.fxml`](src/main/resources/HotelApp/Booking.fxml).
- Check-in / Check-out: cập nhật trạng thái booking và phòng — [`HotelApp.CheckInController`](src/main/java/HotelApp/CheckInController.java), [`HotelApp.CheckOutController`](src/main/java/HotelApp/CheckOutController.java) và các FXML tương ứng.
- Quản lý phòng: thêm phòng, xem danh sách — [`HotelApp.RoomMgmtController`](src/main/java/HotelApp/RoomMgmtController.java) và [`src/main/resources/HotelApp/RoomMgmt.fxml`](src/main/resources/HotelApp/RoomMgmt.fxml).
- Định giá phòng: thiết lập bảng giá theo loại phòng — [`HotelApp.PricingController`](src/main/java/HotelApp/PricingController.java) và [`src/main/resources/HotelApp/Pricing.fxml`](src/main/resources/HotelApp/Pricing.fxml).
- Lưu trữ tạm thời trong bộ nhớ qua repository: [`HotelApp.BookingRepository`](src/main/java/HotelApp/BookingRepository.java), [`HotelApp.RoomRepository`](src/main/java/HotelApp/RoomRepository.java), [`HotelApp.PricingRepository`](src/main/java/HotelApp/PricingRepository.java).
- Kết nối cơ sở dữ liệu (ví dụ): [`HotelApp.db.DButil`](src/main/java/HotelApp/db/DButil.java).

## Cấu trúc chính
- Entry point: [`HotelApp.App`](src/main/java/HotelApp/App.java) — tải giao diện chính [`src/main/resources/HotelApp/Main.fxml`](src/main/resources/HotelApp/Main.fxml) và controller [`HotelApp.MainController`](src/main/java/HotelApp/MainController.java).
- Resources: styles tại [`src/main/resources/HotelApp/styles.css`](src/main/resources/HotelApp/styles.css).
- Maven build: cấu hình trong [`pom.xml`](pom.xml).

## Yêu cầu & chạy
- JDK phù hợp (Java 24 trong project này) và JavaFX.
- Chạy với Maven:
  - mvn clean javafx:run
- Hoặc chạy từ IDE (chạy lớp chính [`HotelApp.App`](src/main/java/HotelApp/App.java)).

## Ghi chú
- Dữ liệu hiện lưu tạm trong các repository nội bộ (danh sách trong bộ nhớ). Cần mở rộng để lưu vào DB thực tế nếu cần — tham khảo [`HotelApp.db.DButil`](src/main/java/HotelApp/db/DButil.java).
- Các file FXML và controller chính:
  - [`src/main/resources/HotelApp/Main.fxml`](src/main/resources/HotelApp/Main.fxml) — giao diện chính
  - [`src/main/java/HotelApp/MainController.java`](src/main/java/HotelApp/MainController.java)
  - [`src/main/resources/HotelApp/Booking.fxml`](src/main/resources/HotelApp/Booking.fxml)
  - [`src/main/resources/HotelApp/RoomMgmt.fxml`](src/main/resources/HotelApp/RoomMgmt.fxml)
  - [`src/main/resources/HotelApp/Pricing.fxml`](src/main/resources/HotelApp/Pricing.fxml)