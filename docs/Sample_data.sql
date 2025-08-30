-- ================== FULL SAMPLE DATA (FIXED) ==================

-- 1. Account
INSERT INTO Account_Management VALUES
('admin', '123456', 1),
('staff1', 'staff123', 0),
('staff2', 'staff234', 0),
('reception1', 'recep123', 0);

-- 2. Room
INSERT INTO Room_Management VALUES
('R1',N'Single','101',N'Standard',500000,0),
('R2',N'Single','102',N'Deluxe',700000,0),
('R3',N'Single','103',N'Premium',900000,0),
('R4',N'Double','201',N'Standard',800000,0),
('R5',N'Double','202',N'Deluxe',1000000,0),
('R6',N'Double','203',N'Premium',1200000,0),
('R7',N'Double','204',N'Superior',1400000,0),
('R8',N'Suite','301',N'Standard',1500000,0),
('R9',N'Suite','302',N'Deluxe',1800000,0),
('R10',N'Suite','303',N'Premium',2200000,0),
('R11',N'Suite','304',N'Royal',3000000,0);

-- 3. Customer
INSERT INTO Customer_Management VALUES
('C1',N'Nguyễn Văn A','1985-01-10','123456789','1','1','0912345678'),
('C2',N'Trần Thị B','1990-05-21','987654321','1','0','0987654321'),
('C3',N'Lê Văn C','1982-07-12','112233445','1','1','0909123456'),
('C4',N'Phạm Thị D','1995-11-01','556677889','1','0','0911223344'),
('C5',N'Ngô Văn E','1978-03-08','667788990','1','1','0933445566'),
('C6',N'Hoàng Thị F','1988-08-18','334455667','1','0','0944556677'),
('C7',N'Đặng Văn G','1992-02-20','998877665','1','1','0955667788'),
('C8',N'Bùi Thị H','1987-09-15','776655443','1','0','0966778899'),
('C9',N'Vũ Văn I','1993-06-05','889900112','1','1','0977889900'),
('C10',N'Phan Thị J','1991-12-25','445566778','1','0','0988990011'),
('C11',N'Trịnh Văn K','1980-04-30','223344556','1','1','0999001122');

-- 4. Service
INSERT INTO Service_Management VALUES
('S1',N'Dọn phòng',50000),
('S2',N'Giặt ủi',70000),
('S3',N'Đưa đón sân bay',150000),
('S4',N'Massage',200000),
('S5',N'Ăn sáng',100000),
('S6',N'Tour du lịch',300000),
('S7',N'Thuê xe',250000),
('S8',N'Dọn minibar',50000),
('S9',N'Spa',400000),
('S10',N'Tư vấn sự kiện',100000),
('S11',N'Ăn tối',150000);

-- 5. Roomtype Amenity
INSERT INTO RoomType_Amenity VALUES
(N'Single', N'Standard', N'Giường đơn, TV, Wifi, Tủ lạnh'),
(N'Single', N'Deluxe', N'Giường đơn, TV, Wifi, Tủ lạnh, Máy sấy'),
(N'Double', N'Standard', N'Giường đôi, TV, Wifi'),
(N'Double', N'Deluxe', N'Giường đôi, TV, Wifi, Mini bar'),
(N'Double', N'Premium', N'Giường đôi, TV, Wifi, Mini bar, Máy pha cà phê'),
(N'Suite', N'Standard', N'Phòng khách, Giường đôi, TV, Wifi'),
(N'Suite', N'Deluxe', N'Phòng khách, Giường đôi, TV, Wifi, Mini bar'),
(N'Suite', N'Premium', N'Phòng khách, Giường đôi, TV, Wifi, Mini bar, Ban công'),
(N'Single', N'Premium', N'Giường đơn, TV, Wifi, Tủ lạnh, Ban công'),
(N'Double', N'Superior', N'Giường đôi, TV, Wifi, Mini bar, Ban công'),
(N'Suite', N'Royal', N'Phòng khách, Giường đôi, TV, Wifi, Mini bar, Ban công, Jacuzzi');

-- 7. Booking
INSERT INTO Booking_Management VALUES
('B1',200000,N'Tiền mặt',2,'2025-08-01','2025-08-10','2025-08-12',N'Nguyễn Văn A','0912345678','2025-08-01','2025-08-01'),
('B2',500000,N'Chuyển khoản',2,'2025-08-02','2025-08-15','2025-08-18',N'Trần Thị B','0987654321','2025-08-02','2025-08-02'),
('B3',300000,N'Tiền mặt',2,'2025-08-03','2025-08-20','2025-08-22',N'Lê Văn C','0909123456','2025-08-03','2025-08-03'),
('B4',400000,N'Ví điện tử',2,'2025-08-04','2025-08-05','2025-08-07',N'Phạm Thị D','0911223344','2025-08-04','2025-08-04'),
('B5',100000,N'Chuyển khoản',2,'2025-08-05','2025-08-08','2025-08-09',N'Ngô Văn E','0933445566','2025-08-05','2025-08-05'),
('B6',250000,N'Tiền mặt',2,'2025-08-06','2025-08-09','2025-08-10',N'Hoàng Thị F','0944556677','2025-08-06','2025-08-06'),
('B7',600000,N'Ví điện tử',2,'2025-08-07','2025-08-12','2025-08-14',N'Đặng Văn G','0955667788','2025-08-07','2025-08-07'),
('B8',350000,N'Tiền mặt',2,'2025-08-08','2025-08-11','2025-08-13',N'Bùi Thị H','0966778899','2025-08-08','2025-08-08'),
('B9',450000,N'Chuyển khoản',2,'2025-08-09','2025-08-15','2025-08-16',N'Vũ Văn I','0977889900','2025-08-09','2025-08-09'),
('B10',500000,N'Tiền mặt',2,'2025-08-10','2025-08-20','2025-08-21',N'Phan Thị J','0988990011','2025-08-10','2025-08-10'),
('B11',550000,N'Ví điện tử',2,'2025-08-11','2025-08-22','2025-08-23',N'Trịnh Văn K','0999001122','2025-08-11','2025-08-11');

-- 8. Booking_Room
INSERT INTO Booking_Room VALUES
('B1','R1'),('B2','R2'),('B3','R3'),('B4','R4'),('B5','R5'),
('B6','R6'),('B7','R7'),('B8','R8'),('B9','R9'),('B10','R10'),('B11','R11');

-- 9. Staying
INSERT INTO Staying_Management VALUES
('ST1','B1','2025-08-10','2025-08-12',N'Tiền mặt',0,1000000,'2025-08-01','2025-08-01'),
('ST2','B2','2025-08-18','2025-08-20',N'Chuyển khoản',0,1500000,'2025-08-02','2025-08-02'),
('ST3','B3','2025-08-22','2025-08-25',N'Tiền mặt',0,1200000,'2025-08-03','2025-08-03'),
('ST4','B4','2025-08-05','2025-08-07',N'Ví điện tử',0,800000,'2025-08-04','2025-08-04'),
('ST5','B5','2025-08-08','2025-08-09',N'Chuyển khoản',0,600000,'2025-08-05','2025-08-05'),
('ST6','B6','2025-08-09','2025-08-10',N'Tiền mặt',0,700000,'2025-08-06','2025-08-06'),
('ST7','B7','2025-08-14','2025-08-16',N'Ví điện tử',0,1300000,'2025-08-07','2025-08-07'),
('ST8','B8','2025-08-13','2025-08-15',N'Tiền mặt',0,900000,'2025-08-08','2025-08-08'),
('ST9','B9','2025-08-16','2025-08-18',N'Chuyển khoản',0,1100000,'2025-08-09','2025-08-09'),
('ST10','B10','2025-08-21','2025-08-23',N'Tiền mặt',0,1400000,'2025-08-10','2025-08-10'),
('ST11','B11','2025-08-23','2025-08-25',N'Ví điện tử',0,1600000,'2025-08-11','2025-08-11');

-- 10. Staying_Room
INSERT INTO Staying_Room VALUES
('ST1','R1'),('ST2','R2'),('ST3','R3'),('ST4','R4'),('ST5','R5'),
('ST6','R6'),('ST7','R7'),('ST8','R8'),('ST9','R9'),('ST10','R10'),('ST11','R11');

-- 11. Staying_Room_Customer
INSERT INTO Staying_Room_Customer VALUES
('ST1','C1','R1'),('ST2','C2','R2'),('ST3','C3','R3'),('ST4','C4','R4'),('ST5','C5','R5'),
('ST6','C6','R6'),('ST7','C7','R7'),('ST8','C8','R8'),('ST9','C9','R9'),('ST10','C10','R10'),('ST11','C11','R11');

-- 12. Staying_Service
INSERT INTO Staying_Service VALUES
('ST1','S1',N'Dọn phòng 2 lần/ngày'),
('ST2','S2',N'Giặt ủi 5kg quần áo'),
('ST3','S3',N'Đưa đón sân bay'),
('ST4','S4',N'Massage thư giãn'),
('ST5','S5',N'Ăn sáng tự chọn'),
('ST6','S6',N'Tour du lịch 1 ngày'),
('ST7','S7',N'Thuê xe 7 chỗ'),
('ST8','S8',N'Dọn minibar'),
('ST9','S9',N'Spa VIP'),
('ST10','S10',N'Tư vấn tổ chức sự kiện'),
('ST11','S11',N'Ăn tối tại nhà hàng');