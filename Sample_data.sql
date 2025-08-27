-- ================== FULL SAMPLE DATA (FIXED) ==================

-- 1. Account
INSERT INTO [dbo].[Account_Management] VALUES
('admin', '123456', 1),
('staff1', 'staff123', 0),
('staff2', 'staff234', 0),
('reception1', 'recep123', 0);

-- 2. Room
INSERT INTO [dbo].[Room_Management] VALUES
('R001', 0, '101', 0, 500000, 0),   -- Standard
('R002', 1, '102', 1, 800000, 0),   -- Deluxe
('R003', 2, '201', 2, 1500000, 0),  -- Suite Premium
('R004', 0, '103', 0, 500000, 0),   -- Standard
('R005', 1, '202', 1, 900000, 0),   -- Deluxe
('R006', 2, '203', 3, 2000000, 0),  -- Suite VIP
('R007', 0, '104', 0, 520000, 1),   -- Standard
('R008', 1, '105', 1, 850000, 0),   -- Deluxe
('R009', 2, '204', 2, 1600000, 1),  -- Suite Premium
('R010', 0, '106', 0, 500000, 0),   -- Standard
('R011', 1, '205', 1, 950000, 1),   -- Deluxe
('R012', 2, '206', 3, 2100000, 0),  -- Suite VIP
('R013', 0, '107', 0, 550000, 1),   -- Standard
('R014', 1, '108', 1, 880000, 0),   -- Deluxe
('R015', 2, '207', 2, 1700000, 1),  -- Suite Premium
('R016', 2, '208', 3, 2200000, 0);  -- Suite VIP

-- 3. Customer
INSERT INTO [dbo].[Customer_Management] VALUES
('C001', 'Nguyen Van A', '1995-05-20', '123456789', 1, 1, '0901234567'),
('C002', 'John Smith', '1988-09-15', 'P1234567', 0, 1, '+12015550123'),
('C003', N'Le Thi B', '1998-12-01', '987654321', 1, 0, '0912345678'),
('C004', N'Pham Van C', '1990-07-11', '2468101214', 1, 1, '0923456789'),
('C005', 'Alice Johnson', '1985-03-09', 'P7654321', 0, 0, '+12015550999');

-- 4. Service
INSERT INTO [dbo].[Service_Management] VALUES
('S001', 'Laundry', 50000),
('S002', 'Breakfast Buffet', 100000),
('S003', 'Airport Pickup', 300000),
('S004', 'Spa', 400000),
('S005', 'Dinner Set', 250000);

-- 5. Amenity
INSERT INTO [dbo].[Amenity_Management] VALUES
('A001', 'TV', 'Smart TV 42 inch', 20, 20),
('A002', 'Air Conditioner', 'Daikin Inverter', 15, 15),
('A003', 'Mini Bar', 'With free drinks', 10, 10),
('A004', 'Hair Dryer', 'Philips 2000W', 8, 8),
('A005', 'Kettle', 'Electric kettle 1.5L', 12, 12);

-- 6. Room_Amenity
INSERT INTO [dbo].[Room_Amenity] VALUES
('R001', 'A001', 1),
('R001', 'A002', 1),
('R002', 'A001', 1),
('R002', 'A002', 1),
('R002', 'A003', 1),
('R003', 'A001', 2),
('R003', 'A002', 2),
('R003', 'A003', 1);

-- 7. Booking
INSERT INTO [dbo].[Booking_Management] VALUES
('B001', 200000, 'Cash', 2, GETDATE(), '2025-09-01', '2025-09-03', 'Nguyen Van A', '0901234567', GETDATE(), GETDATE()),
('B002', 300000, 'Credit Card', 2, GETDATE(), '2025-09-05', '2025-09-08', 'John Smith', '+12015550123', GETDATE(), GETDATE()),
('B003', 150000, 'Cash', 3, GETDATE(), '2025-09-10', '2025-09-12', 'Le Thi B', '0912345678', GETDATE(), GETDATE()),
('B004', 0, 'Cash', 0, GETDATE(), '2025-09-15', '2025-09-17', 'Alice Johnson', '+12015550999', GETDATE(), GETDATE()),
('B005', 300000, 'Credit Card', 4, GETDATE(), '2025-09-05', '2025-09-07', 'John Smith', '+12015550123', GETDATE(), GETDATE());  -- ID mới

-- 8. Booking_Room
INSERT INTO [dbo].[Booking_Room] VALUES
('B001', 'R001'),
('B002', 'R002'),
('B003', 'R004'),
('B004', 'R005'),
('B005', 'R002'),
('B005', 'R003');

-- 9. Staying
INSERT INTO [dbo].[Staying_Management] VALUES
('ST001', 'B001', '2025-09-01 14:00:00', '2025-09-03 12:00:00', 'Cash', 0, 1000000, GETDATE(), GETDATE()),
('ST002', NULL, '2025-08-20 15:00:00', '2025-08-22 11:00:00', 'Credit Card', 1, 1600000, GETDATE(), GETDATE());

-- 10. Staying_Room
INSERT INTO [dbo].[Staying_Room] VALUES
('ST001', 'R001'),
('ST002', 'R002');

-- 11. Staying_Room_Customer
INSERT INTO [dbo].[Staying_Room_Customer] VALUES
('ST001', 'C001', 'R001'),
('ST002', 'C002', 'R002');

-- 12. Staying_Service
INSERT INTO [dbo].[Staying_Service] VALUES
('ST001', 'S001', 'Laundry 2 items'),
('ST001', 'S002', 'Breakfast for 2 days'),
('ST002', 'S003', 'Airport pickup from SGN');
