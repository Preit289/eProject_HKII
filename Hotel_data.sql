USE MASTER
GO
-- Drop database if exists
IF DB_ID('HotelMng') IS NOT NULL
BEGIN
    ALTER DATABASE HotelMng SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE HotelMng;
END;
GO

-- Create database
CREATE DATABASE HotelMng;
GO

-- Use database
USE HotelMng;
GO

-- Create schema dbo if not exists
IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = 'dbo')
BEGIN
    EXEC('CREATE SCHEMA [dbo]');
END;
GO

-- ================== TABLES ==================
CREATE TABLE [dbo].[Staying_Management] (
    [Staying_id] nvarchar(100) NOT NULL,
    [Booking_id] nvarchar(100),
    [Checkin_date] datetime NOT NULL,
    [Checkout_date] datetime NOT NULL,
    [Payment_method] nvarchar(100) NOT NULL,
    -- 0 = occupied, 1 = checked out
    [Staying_status] tinyint NOT NULL DEFAULT 0,
    [Total_amount] int NOT NULL DEFAULT 0,
    [Created_at] datetime NOT NULL,
    [Updated_at] datetime NOT NULL,
    PRIMARY KEY ([Staying_id])
);

CREATE TABLE [dbo].[Staying_Service] (
    [Staying_id] nvarchar(100) NOT NULL,
    [Service_id] nvarchar(100) NOT NULL,
    [Service_description] nvarchar(100),
    PRIMARY KEY ([Staying_id], [Service_id])
);

CREATE TABLE [dbo].[Room_Amenity] (
    [Room_id] nvarchar(100) NOT NULL,
    [Amenity_id] nvarchar(100) NOT NULL,
    [Quantity] int,
    PRIMARY KEY ([Room_id], [Amenity_id])
);

CREATE TABLE [dbo].[Account_Management] (
    [username] nvarchar(100) NOT NULL,
    [pass] nvarchar(100) NOT NULL,
    [is_admin] bit NOT NULL,
    PRIMARY KEY ([username])
);

CREATE TABLE [dbo].[Room_Management] (
    [Room_id] nvarchar(100) NOT NULL,
    -- 0 = single, 1 = double, 2 = suite
    [Room_category] tinyint NOT NULL,
    [Room_num] nvarchar(100),
    [Room_quality] nvarchar(50) NOT NULL,
    [Room_price] int NOT NULL,
    -- 0 = Empty, 1 = Occupied, 2 = Cleaning
    [Room_status] tinyint NOT NULL,
    PRIMARY KEY ([Room_id])
);

CREATE TABLE [dbo].[Customer_Management] (
    [Customer_id] nvarchar(100) NOT NULL,
    [Customer_name] nvarchar(100) NOT NULL,
    [DOB] date NOT NULL,
    [Citizen_identity_number] nvarchar(50) NOT NULL,
    -- 1 = VN, 0 = foreigner
    [Is_foreigner] bit NOT NULL,
    -- 1 = Male, 0 = Female
    [Gender] bit NOT NULL,
    [Phone_num] nvarchar(20) NOT NULL,
    PRIMARY KEY ([Customer_id])
);

CREATE TABLE [dbo].[Booking_Management] (
    [Booking_id] nvarchar(100) NOT NULL,
    [Deposit_amount] int NOT NULL,
    [Payment_method] nvarchar(100) NOT NULL,
    -- 4 = Room received, 3 = Late arrival, 2 = Booking active,
    -- 1 = Cancelled by hotel, 0 = Cancelled by customer
    [Booking_status] tinyint NOT NULL,
    [Booking_date] datetime NOT NULL,
    [Checkin_date] datetime NOT NULL,
    [Checkout_date] datetime NOT NULL,
    [Book_by] nvarchar(100) NOT NULL,
    [Book_contact] nvarchar(50) NOT NULL,
    [Created_at] datetime NOT NULL,
    [Updated_at] datetime NOT NULL,
    PRIMARY KEY ([Booking_id])
);

CREATE TABLE [dbo].[Service_Management] (
    [Service_id] nvarchar(100) NOT NULL,
    [Service_name] nvarchar(100) NOT NULL,
    [Service_price] int NOT NULL,
    PRIMARY KEY ([Service_id])
);

CREATE TABLE [dbo].[Amenity_Management] (
    [Amenity_id] nvarchar(100) NOT NULL,
    [Amenity_name] nvarchar(100) NOT NULL,
    [Amenity_description] nvarchar(100) NOT NULL,
    [Total_quantity] int,
    [Available_quantity] int,
    PRIMARY KEY ([Amenity_id])
);

CREATE TABLE [dbo].[Staying_Room_Customer] (
    [Staying_id] nvarchar(100) NOT NULL,
    [Customer_id] nvarchar(100) NOT NULL,
    [Room_id] nvarchar(100) NOT NULL,
    PRIMARY KEY ([Staying_id], [Customer_id], [Room_id])
);

CREATE TABLE [dbo].[Booking_Room] (
    [Booking_id] nvarchar(100) NOT NULL,
    [Room_id] nvarchar(100) NOT NULL,
    PRIMARY KEY ([Booking_id], [Room_id])
);

CREATE TABLE [dbo].[Staying_Room] (
    [Staying_id] nvarchar(100) NOT NULL,
    [Room_id] nvarchar(100) NOT NULL,
    PRIMARY KEY ([Staying_id], [Room_id])
);

-- ================== FOREIGN KEYS ==================
ALTER TABLE [dbo].[Staying_Service] 
    ADD CONSTRAINT [FK_Staying_Service_Service] FOREIGN KEY([Service_id]) REFERENCES [dbo].[Service_Management]([Service_id]);

ALTER TABLE [dbo].[Room_Amenity] 
    ADD CONSTRAINT [FK_Room_Amenity_Amenity] FOREIGN KEY([Amenity_id]) REFERENCES [dbo].[Amenity_Management]([Amenity_id]);

ALTER TABLE [dbo].[Booking_Room] 
    ADD CONSTRAINT [FK_Booking_Room_Room] FOREIGN KEY([Room_id]) REFERENCES [dbo].[Room_Management]([Room_id]);

ALTER TABLE [dbo].[Staying_Room_Customer] 
    ADD CONSTRAINT [FK_Staying_Customer] FOREIGN KEY([Customer_id]) REFERENCES [dbo].[Customer_Management]([Customer_id]);

ALTER TABLE [dbo].[Staying_Management] 
    ADD CONSTRAINT [FK_Staying_Booking] FOREIGN KEY([Booking_id]) REFERENCES [dbo].[Booking_Management]([Booking_id]);

ALTER TABLE [dbo].[Staying_Room] 
    ADD CONSTRAINT [FK_Staying_Room_Staying] FOREIGN KEY([Staying_id]) REFERENCES [dbo].[Staying_Management]([Staying_id]);

ALTER TABLE [dbo].[Staying_Room] 
    ADD CONSTRAINT [FK_Staying_Room_Room] FOREIGN KEY([Room_id]) REFERENCES [dbo].[Room_Management]([Room_id]);

ALTER TABLE [dbo].[Staying_Room_Customer] 
    ADD CONSTRAINT [FK_Staying_Room_Customer] FOREIGN KEY([Staying_id], [Room_id]) REFERENCES [dbo].[Staying_Room]([Staying_id], [Room_id]);

ALTER TABLE [dbo].[Booking_Room] 
    ADD CONSTRAINT [FK_Booking_Room_Booking] FOREIGN KEY([Booking_id]) REFERENCES [dbo].[Booking_Management]([Booking_id]);

ALTER TABLE [dbo].[Staying_Service] 
    ADD CONSTRAINT [FK_Staying_Service_Staying] FOREIGN KEY([Staying_id]) REFERENCES [dbo].[Staying_Management]([Staying_id]);

ALTER TABLE [dbo].[Room_Amenity] 
    ADD CONSTRAINT [FK_Room_Amenity_Room] FOREIGN KEY([Room_id]) REFERENCES [dbo].[Room_Management]([Room_id]);

-- ================== FULL SAMPLE DATA (FIXED) ==================

-- 1. Account
INSERT INTO [dbo].[Account_Management] VALUES 
('admin', '123456', 1),
('staff1', 'staff123', 0),
('staff2', 'staff234', 0),
('reception1', 'recep123', 0);

-- 2. Room
INSERT INTO [dbo].[Room_Management] VALUES 
('R001', 0, '101', 'Standard', 500000, 0),
('R002', 1, '102', 'Deluxe', 800000, 0),
('R003', 2, '201', 'Suite Premium', 1500000, 0),
('R004', 0, '103', 'Standard', 500000, 0),
('R005', 1, '202', 'Deluxe', 900000, 0),
('R006', 2, '203', 'Suite VIP', 2000000, 0);

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
