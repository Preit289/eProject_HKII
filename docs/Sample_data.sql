-- ================== FULL SAMPLE DATA (FK SAFE) ==================

-- 1. RoomType_Amenity (phải insert trước Room_Management)
INSERT INTO RoomType_Amenity VALUES
-- Single Rooms
(N'Single', N'Standard', 1, N'Giường đơn, TV, Wifi, Tủ lạnh'),
(N'Single', N'Deluxe', 1, N'Giường đơn, TV, Wifi, Tủ lạnh, Máy sấy'),
(N'Single', N'Premium', 1, N'Giường đơn, TV, Wifi, Tủ lạnh, Ban công'),
(N'Single', N'Superior', 1, N'Giường đơn, TV, Wifi, Tủ lạnh, Ban công, Máy pha cà phê'),

-- Double Rooms
(N'Double', N'Standard', 2, N'Giường đôi, TV, Wifi, Tủ lạnh'),
(N'Double', N'Deluxe', 2, N'Giường đôi, TV, Wifi, Tủ lạnh, Mini bar'),
(N'Double', N'Premium', 2, N'Giường đôi, TV, Wifi, Tủ lạnh, Mini bar, Máy pha cà phê'),
(N'Double', N'Superior', 2, N'Giường đôi, TV, Wifi, Tủ lạnh, Mini bar, Ban công, Máy pha cà phê'),

-- Suite Rooms
(N'Suite', N'Standard', 2, N'Phòng khách, Giường đôi, TV, Wifi, Tủ lạnh'),
(N'Suite', N'Deluxe', 2, N'Phòng khách, Giường đôi, TV, Wifi, Tủ lạnh, Mini bar'),
(N'Suite', N'Premium', 2, N'Phòng khách, Giường đôi, TV, Wifi, Tủ lạnh, Mini bar, Ban công'),
(N'Suite', N'Superior', 2, N'Phòng khách, Giường đôi, TV, Wifi, Tủ lạnh, Mini bar, Ban công, Máy pha cà phê'),
(N'Suite', N'Royal', 2, N'Phòng khách, Giường đôi, TV, Wifi, Tủ lạnh, Mini bar, Ban công, Jacuzzi');

-- 2. Room_Management
INSERT INTO Room_Management VALUES
('R1', N'Single','101', N'Standard', 500000, 0),
('R2', N'Single','102', N'Deluxe', 700000, 0),
('R3', N'Single','103', N'Premium', 900000, 0),
('R4', N'Double','201', N'Standard', 800000, 0),
('R5', N'Double','202', N'Deluxe', 1000000, 0),
('R6', N'Double','203', N'Premium', 1200000, 0),
('R7', N'Double','204', N'Superior', 1400000, 0),
('R8', N'Suite','301', N'Standard', 1500000, 0),
('R9', N'Suite','302', N'Deluxe', 1800000, 0),
('R10', N'Suite','303', N'Premium', 2200000, 0),
('R11', N'Suite','304', N'Royal', 3000000, 0);

-- 3. Account_Management
INSERT INTO Account_Management VALUES
('admin', '123456', 1),
('staff1', 'staff123', 0),
('staff2', 'staff234', 0),
('reception1', 'recep123', 0);

-- 4. Customer_Management
INSERT INTO Customer_Management VALUES
('C1',N'Nguyễn Văn A','1985-01-10','123456789',0,1,0,'0912345678'),
('C2',N'Trần Thị B','1990-05-21','987654321',0,0,0,'0987654321'),
('C3',N'Lê Văn C','1982-07-12','112233445',0,1,0,'0909123456'),
('C4',N'Phạm Thị D','1995-11-01','556677889',0,0,0,'0911223344'),
('C5',N'Ngô Văn E','1978-03-08','667788990',0,1,0,'0933445566'),
('C6',N'Hoàng Thị F','1988-08-18','334455667',0,0,0,'0944556677'),
('C7',N'Đặng Văn G','1992-02-20','998877665',0,1,0,'0955667788'),
('C8',N'Bùi Thị H','1987-09-15','776655443',0,0,0,'0966778899'),
('C9',N'Vũ Văn I','1993-06-05','889900112',0,1,0,'0977889900'),
('C10',N'Phan Thị J','1991-12-25','445566778',0,0,0,'0988990011'),
('C11',N'Trịnh Văn K','1980-04-30','223344556',0,1,0,'0999001122');

-- 5. Service_Management (with description)
INSERT INTO Service_Management VALUES
('S1', N'Dọn phòng', 50000, N'Dọn phòng 2 lần/ngày'),
('S2', N'Giặt ủi', 70000, N'Giặt ủi 5kg quần áo'),
('S3', N'Đưa đón sân bay', 150000, N'Đưa đón sân bay tận nơi'),
('S4', N'Massage', 200000, N'Massage thư giãn 60 phút'),
('S5', N'Ăn sáng', 100000, N'Ăn sáng buffet tự chọn'),
('S6', N'Tour du lịch', 300000, N'Tour du lịch 1 ngày'),
('S7', N'Thuê xe', 250000, N'Thuê xe 7 chỗ cả ngày'),
('S8', N'Dọn minibar', 50000, N'Thêm đồ uống và snack vào minibar'),
('S9', N'Spa', 400000, N'Spa VIP 90 phút'),
('S10', N'Tư vấn sự kiện', 100000, N'Tư vấn tổ chức sự kiện riêng'),
('S11', N'Ăn tối', 150000, N'Ăn tối tại nhà hàng khách sạn');

-- 6. Booking_Management
INSERT INTO Booking_Management VALUES
('B1',200000,N'Tiền mặt',2,'2025-08-01','2025-08-10','2025-08-12','admin','0912345678','2025-08-01','2025-08-01','admin'),
('B2',500000,N'Chuyển khoản',2,'2025-08-02','2025-08-15','2025-08-18','staff1','0987654321','2025-08-02','2025-08-02','staff1'),
('B3',300000,N'Tiền mặt',2,'2025-08-03','2025-08-20','2025-08-22','staff2','0909123456','2025-08-03','2025-08-03','staff2'),
('B4',400000,N'Ví điện tử',2,'2025-08-04','2025-08-05','2025-08-07','reception1','0911223344','2025-08-04','2025-08-04','reception1'),
('B5',100000,N'Chuyển khoản',2,'2025-08-05','2025-08-08','2025-08-09','admin','0933445566','2025-08-05','2025-08-05','admin'),
('B6',250000,N'Tiền mặt',2,'2025-08-06','2025-08-09','2025-08-10','staff1','0944556677','2025-08-06','2025-08-06','staff1'),
('B7',600000,N'Ví điện tử',2,'2025-08-07','2025-08-12','2025-08-14','staff2','0955667788','2025-08-07','2025-08-07','staff2'),
('B8',350000,N'Tiền mặt',2,'2025-08-08','2025-08-11','2025-08-13','reception1','0966778899','2025-08-08','2025-08-08','reception1'),
('B9',450000,N'Chuyển khoản',2,'2025-08-09','2025-08-15','2025-08-16','admin','0977889900','2025-08-09','2025-08-09','admin'),
('B10',500000,N'Tiền mặt',2,'2025-08-10','2025-08-20','2025-08-21','staff1','0988990011','2025-08-10','2025-08-10','staff1'),
('B11',550000,N'Ví điện tử',2,'2025-08-11','2025-08-22','2025-08-23','staff2','0999001122','2025-08-11','2025-08-11','staff2');

-- 7. Booking_Room
INSERT INTO Booking_Room VALUES
('B1','R1'),('B2','R2'),('B3','R3'),('B4','R4'),('B5','R5'),
('B6','R6'),('B7','R7'),('B8','R8'),('B9','R9'),('B10','R10'),('B11','R11');

-- 8. Staying_Management
INSERT INTO Staying_Management VALUES
('ST1','B1','2025-08-10','2025-08-12',N'Tiền mặt',0,1000000,'2025-08-01','2025-08-01','admin'),
('ST2','B2','2025-08-18','2025-08-20',N'Chuyển khoản',0,1500000,'2025-08-02','2025-08-02','staff1'),
('ST3','B3','2025-08-22','2025-08-25',N'Tiền mặt',0,1200000,'2025-08-03','2025-08-03','staff2'),
('ST4','B4','2025-08-05','2025-08-07',N'Ví điện tử',0,800000,'2025-08-04','2025-08-04','reception1'),
('ST5','B5','2025-08-08','2025-08-09',N'Chuyển khoản',0,600000,'2025-08-05','2025-08-05','admin'),
('ST6','B6','2025-08-09','2025-08-10',N'Tiền mặt',0,700000,'2025-08-06','2025-08-06','staff1'),
('ST7','B7','2025-08-14','2025-08-16',N'Ví điện tử',0,1300000,'2025-08-07','2025-08-07','staff2'),
('ST8','B8','2025-08-13','2025-08-15',N'Tiền mặt',0,900000,'2025-08-08','2025-08-08','reception1'),
('ST9','B9','2025-08-16','2025-08-18',N'Chuyển khoản',0,1100000,'2025-08-09','2025-08-09','admin'),
('ST10','B10','2025-08-21','2025-08-23',N'Tiền mặt',0,1400000,'2025-08-10','2025-08-10','staff1'),
('ST11','B11','2025-08-23','2025-08-25',N'Ví điện tử',0,1600000,'2025-08-11','2025-08-11','staff2');

-- 9. Staying_Room_Customer
INSERT INTO Staying_Room_Customer VALUES
('ST1','R1','C1'),('ST2','R2','C2'),('ST3','R3','C3'),
('ST4','R4','C4'),('ST5','R5','C5'),('ST6','R6','C6'),
('ST7','R7','C7'),('ST8','R8','C8'),('ST9','R9','C9'),
('ST10','R10','C10'),('ST11','R11','C11');

-- 10. Staying_Room_Service
INSERT INTO Staying_Room_Service VALUES
('ST1','R1','S1',1),
('ST2','R2','S2',2),
('ST3','R3','S3',1),
('ST4','R4','S4',1),
('ST5','R5','S5',2),
('ST6','R6','S6',1),
('ST7','R7','S7',1),
('ST8','R8','S8',1),
('ST9','R9','S9',1),
('ST10','R10','S10',1),
('ST11','R11','S11',1);
