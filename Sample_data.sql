-- ================== FULL SAMPLE DATA (FIXED) ==================

-- 1. Account
INSERT INTO [dbo].[Account_Management] VALUES
('admin', '123456', 1),
('staff1', 'staff123', 0),
('staff2', 'staff234', 0),
('reception1', 'recep123', 0);

-- 2. Room
INSERT INTO [dbo].[Room_Management] VALUES
('R1', 0, '101', 0, 500000, 0),   -- Standard
('R2', 1, '102', 1, 800000, 0),   -- Deluxe
('R3', 2, '201', 2, 1500000, 0),  -- Suite Premium
('R4', 0, '103', 0, 500000, 0),   -- Standard
('R5', 1, '202', 1, 900000, 0),   -- Deluxe
('R6', 2, '203', 3, 2000000, 0),  -- Suite VIP
('R7', 0, '104', 0, 520000, 1),   -- Standard
('R8', 1, '105', 1, 850000, 0),   -- Deluxe
('R9', 2, '204', 2, 1600000, 1),  -- Suite Premium
('R10', 0, '106', 0, 500000, 0),   -- Standard
('R11', 1, '205', 1, 950000, 1),   -- Deluxe
('R12', 2, '206', 3, 2100000, 0),  -- Suite VIP
('R13', 0, '107', 0, 550000, 1),   -- Standard
('R14', 1, '108', 1, 880000, 0),   -- Deluxe
('R15', 2, '207', 2, 1700000, 1),  -- Suite Premium
('R16', 2, '208', 3, 2200000, 0);  -- Suite VIP

-- 3. Customer
INSERT INTO [dbo].[Customer_Management] VALUES
('C1', 'Nguyen Van A', '1995-05-20', '123456789', 1, 1, '0901234567'),
('C2', 'John Smith', '1988-09-15', 'P1234567', 0, 1, '+12015550123'),
('C3', N'Le Thi B', '1998-12-01', '987654321', 1, 0, '0912345678'),
('C4', N'Pham Van C', '1990-07-11', '2468101214', 1, 1, '0923456789'),
('C5', 'Alice Johnson', '1985-03-09', 'P7654321', 0, 0, '+12015550999');

-- 4. Service
INSERT INTO [dbo].[Service_Management] VALUES
('S1', 'Laundry', 50000),
('S2', 'Breakfast Buffet', 100000),
('S3', 'Airport Pickup', 300000),
('S4', 'Spa', 400000),
('S5', 'Dinner Set', 250000);

-- 5. Amenity
INSERT INTO [dbo].[Amenity_Management] VALUES
('A1', 'TV', 'Smart TV 42 inch', 20, 20),
('A2', 'Air Conditioner', 'Daikin Inverter', 15, 15),
('A3', 'Mini Bar', 'With free drinks', 10, 10),
('A4', 'Hair Dryer', 'Philips 2000W', 8, 8),
('A5', 'Kettle', 'Electric kettle 1.5L', 12, 12);

-- 6. Room_Amenity
INSERT INTO [dbo].[Room_Amenity] VALUES
('R1', 'A1', 1),
('R1', 'A2', 1),
('R2', 'A1', 1),
('R2', 'A2', 1),
('R2', 'A3', 1),
('R3', 'A1', 2),
('R3', 'A2', 2),
('R3', 'A3', 1);

-- 7. Booking
INSERT INTO [dbo].[Booking_Management] VALUES
('B1', 200000, 'Cash', 2, GETDATE(), '2025-09-01', '2025-09-03', 'Nguyen Van A', '0901234567', GETDATE(), GETDATE()),
('B2', 300000, 'Credit Card', 2, GETDATE(), '2025-09-05', '2025-09-08', 'John Smith', '+12015550123', GETDATE(), GETDATE()),
('B3', 150000, 'Cash', 3, GETDATE(), '2025-09-10', '2025-09-12', 'Le Thi B', '0912345678', GETDATE(), GETDATE()),
('B4', 0, 'Cash', 0, GETDATE(), '2025-09-15', '2025-09-17', 'Alice Johnson', '+12015550999', GETDATE(), GETDATE()),
('B5', 300000, 'Credit Card', 4, GETDATE(), '2025-09-05', '2025-09-07', 'John Smith', '+12015550123', GETDATE(), GETDATE());  -- ID mới

-- 8. Booking_Room
INSERT INTO [dbo].[Booking_Room] VALUES
('B1', 'R1'),
('B2', 'R2'),
('B3', 'R4'),
('B4', 'R5'),
('B5', 'R2'),
('B5', 'R3');

-- 9. Staying
INSERT INTO [dbo].[Staying_Management] VALUES
('ST1', 'B1', '2025-09-01 14:00:00', '2025-09-03 12:00:00', 'Cash', 0, 1000000, GETDATE(), GETDATE()),
('ST2', NULL, '2025-08-20 15:00:00', '2025-08-22 11:00:00', 'Credit Card', 1, 1600000, GETDATE(), GETDATE());

-- 10. Staying_Room
INSERT INTO [dbo].[Staying_Room] VALUES
('ST1', 'R1'),
('ST2', 'R2');

-- 11. Staying_Room_Customer
INSERT INTO [dbo].[Staying_Room_Customer] VALUES
('ST1', 'C1', 'R1'),
('ST2', 'C2', 'R2');

-- 12. Staying_Service
INSERT INTO [dbo].[Staying_Service] VALUES
('ST1', 'S1', 'Laundry 2 items'),
('ST1', 'S2', 'Breakfast for 2 days'),
('ST2', 'S3', 'Airport pickup from SGN');
