# Hotel Management System - System Overview

## Tổng quan hệ thống

Hệ thống Quản lý Khách sạn đã được cải tiến hoàn toàn để đáp ứng đầy đủ cấu trúc database từ file `Hotel_data.sql`. Hệ thống bao gồm các chức năng chính sau:

## Cấu trúc Database

### 1. Bảng chính (Core Tables)

- **Room_Management**: Quản lý phòng (ID, loại, số phòng, chất lượng, giá, trạng thái)
- **Customer_Management**: Quản lý khách hàng (thông tin cá nhân, quốc tịch, giới tính)
- **Booking_Management**: Quản lý đặt phòng (thông tin đặt phòng, trạng thái, thanh toán)
- **Staying_Management**: Quản lý thời gian lưu trú (check-in, check-out, thanh toán)
- **Service_Management**: Quản lý dịch vụ (tên dịch vụ, giá)
- **Amenity_Management**: Quản lý tiện ích (tên, mô tả, số lượng)
- **Account_Management**: Quản lý tài khoản (username, password, quyền admin)

### 2. Bảng quan hệ (Relationship Tables)

- **Staying_Room_Customer**: Liên kết khách hàng, phòng và thời gian lưu trú
- **Staying_Service**: Dịch vụ sử dụng trong thời gian lưu trú
- **Room_Amenity**: Tiện ích của từng phòng
- **Booking_Room**: Liên kết đặt phòng và phòng
- **Staying_Room**: Liên kết thời gian lưu trú và phòng

## Kiến trúc hệ thống

### 1. Model Layer

- **Room.java**: Model cho phòng
- **Customer.java**: Model cho khách hàng
- **Booking.java**: Model cho đặt phòng
- **Staying.java**: Model cho thời gian lưu trú
- **Service.java**: Model cho dịch vụ
- **Amenity.java**: Model cho tiện ích
- **Account.java**: Model cho tài khoản

### 2. Repository Layer

- **RoomRepository**: Xử lý CRUD cho phòng
- **CustomerRepository**: Xử lý CRUD cho khách hàng
- **BookingRepository**: Xử lý CRUD cho đặt phòng
- **StayingRepository**: Xử lý CRUD cho thời gian lưu trú
- **ServiceRepository**: Xử lý CRUD cho dịch vụ
- **AmenityRepository**: Xử lý CRUD cho tiện ích
- **AccountRepository**: Xử lý authentication
- **StayingServiceRepository**: Xử lý dịch vụ trong thời gian lưu trú
- **RoomAmenityRepository**: Xử lý tiện ích của phòng
- **StayingRoomCustomerRepository**: Xử lý mối quan hệ khách hàng-phòng-lưu trú

### 3. Service Layer

- **HotelService**: Xử lý business logic chung
- **AuthService**: Xử lý authentication và authorization
- **ReportService**: Xử lý báo cáo và thống kê

### 4. Utility Layer

- **IdGenerator**: Tạo ID tự động cho các entity
- **ValidationUtil**: Validation cho input
- **CommonUtil**: Các hàm tiện ích chung

## Chức năng chính

### 1. Quản lý Phòng

- Xem danh sách phòng
- Thêm/sửa/xóa phòng
- Cập nhật trạng thái phòng (Empty, Occupied, Cleaning)
- Phân loại phòng (Single, Double, Suite)
- Quản lý tiện ích của phòng

### 2. Quản lý Khách hàng

- Thêm/sửa/xóa thông tin khách hàng
- Tìm kiếm khách hàng
- Phân loại khách hàng (Việt Nam/Nước ngoài)
- Quản lý thông tin cá nhân

### 3. Quản lý Đặt phòng

- Tạo đặt phòng mới
- Cập nhật trạng thái đặt phòng
- Quản lý thông tin thanh toán
- Theo dõi lịch sử đặt phòng

### 4. Quản lý Check-in/Check-out

- Xử lý check-in khách hàng
- Xử lý check-out và thanh toán
- Quản lý thời gian lưu trú
- Theo dõi trạng thái phòng

### 5. Quản lý Dịch vụ

- Thêm/sửa/xóa dịch vụ
- Quản lý giá dịch vụ
- Theo dõi dịch vụ sử dụng

### 6. Quản lý Tiện ích

- Thêm/sửa/xóa tiện ích
- Quản lý số lượng tiện ích
- Phân bổ tiện ích cho phòng

### 7. Báo cáo và Thống kê

- Thống kê phòng (tỷ lệ sử dụng, phân loại)
- Thống kê đặt phòng (số lượng, tỷ lệ hủy)
- Thống kê doanh thu
- Báo cáo khách hàng
- Báo cáo sử dụng phòng

### 8. Quản lý Tài khoản

- Đăng nhập/đăng xuất
- Phân quyền (Admin/Staff)
- Thay đổi mật khẩu
- Quản lý tài khoản

## Cách sử dụng

### 1. Khởi tạo Database

```sql
-- Chạy file Hotel_data.sql để tạo database và dữ liệu mẫu
-- Kết nối database qua DButil.java
```

### 2. Chạy ứng dụng

```bash
# Compile và chạy
javac -cp "lib/*" src/main/java/HotelApp/*.java
java -cp "lib/*:src/main/java" HotelApp.App
```

### 3. Đăng nhập

- **Admin**: username: `admin`, password: `123456`
- **Staff**: username: `staff1`, password: `staff123`

## Tính năng nổi bật

1. **Hệ thống phân quyền**: Admin có toàn quyền, Staff có quyền hạn chế
2. **Validation đầy đủ**: Kiểm tra tính hợp lệ của tất cả input
3. **Xử lý lỗi**: Exception handling cho database operations
4. **Business Logic**: Tự động tính toán giá, kiểm tra tính khả dụng của phòng
5. **Báo cáo chi tiết**: Thống kê đa dạng cho quản lý
6. **Mở rộng**: Dễ dàng thêm tính năng mới

## Cải tiến so với phiên bản cũ

1. **Cấu trúc rõ ràng**: Tách biệt rõ ràng các layer
2. **Database đầy đủ**: Sử dụng đầy đủ cấu trúc database
3. **Business Logic**: Xử lý logic nghiệp vụ phức tạp
4. **Validation**: Kiểm tra tính hợp lệ toàn diện
5. **Error Handling**: Xử lý lỗi chuyên nghiệp
6. **Scalability**: Dễ dàng mở rộng và bảo trì

## Hướng phát triển

1. **Giao diện người dùng**: Cải thiện UI/UX
2. **API REST**: Tạo REST API cho mobile app
3. **Real-time**: Cập nhật real-time cho trạng thái phòng
4. **Payment Integration**: Tích hợp thanh toán online
5. **Multi-language**: Hỗ trợ đa ngôn ngữ
6. **Cloud Deployment**: Triển khai trên cloud

## Liên hệ

Để biết thêm thông tin hoặc hỗ trợ, vui lòng liên hệ team phát triển.
