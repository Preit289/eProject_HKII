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
    [Payment_method] nvarchar(50) NOT NULL,
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
    -- each category room have different quality name
    [Room_quality] tinyint NOT NULL,
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
    [Payment_method] nvarchar(50) NOT NULL,
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

GO

-- ================== FUNCTION ==================
-- Room_ID
CREATE FUNCTION dbo.fn_GenerateNextRoomID()
RETURNS NVARCHAR(20)
AS
BEGIN
    DECLARE @NextID NVARCHAR(20);
    DECLARE @MaxNum INT;

    -- Lấy số lớn nhất từ Room_id (bỏ chữ 'R')
    SELECT @MaxNum = MAX(CAST(SUBSTRING(Room_id, 2, LEN(Room_id)) AS INT))
    FROM Room_Management;

    -- Nếu chưa có dữ liệu thì bắt đầu từ 1
    IF @MaxNum IS NULL
        SET @NextID = 'R1';
    ELSE
        SET @NextID = 'R' + CAST(@MaxNum + 1 AS NVARCHAR(20));

    RETURN @NextID;
END;
GO
-- Amenity_ID
CREATE FUNCTION dbo.fn_GenerateNextAmenityID()
RETURNS NVARCHAR(20)
AS
BEGIN
    DECLARE @NextID NVARCHAR(20);
    DECLARE @MaxNum INT;

    -- Lấy số lớn nhất từ Room_id (bỏ chữ 'R')
    SELECT @MaxNum = MAX(CAST(SUBSTRING(Amenity_id, 2, LEN(Amenity_id)) AS INT))
    FROM Amenity_Management;

    -- Nếu chưa có dữ liệu thì bắt đầu từ 1
    IF @MaxNum IS NULL
        SET @NextID = 'A1';
    ELSE
        SET @NextID = 'A' + CAST(@MaxNum + 1 AS NVARCHAR(20));

    RETURN @NextID;
END;
GO
-- Customer_ID
CREATE FUNCTION dbo.fn_GenerateNextCustomerID()
RETURNS NVARCHAR(20)
AS
BEGIN
    DECLARE @NextID NVARCHAR(20);
    DECLARE @MaxNum INT;

    -- Lấy số lớn nhất từ Room_id (bỏ chữ 'R')
    SELECT @MaxNum = MAX(CAST(SUBSTRING(Customer_id, 2, LEN(Customer_id)) AS INT))
    FROM Customer_Management;

    -- Nếu chưa có dữ liệu thì bắt đầu từ 1
    IF @MaxNum IS NULL
        SET @NextID = 'C1';
    ELSE
        SET @NextID = 'C' + CAST(@MaxNum + 1 AS NVARCHAR(20));

    RETURN @NextID;
END;
GO
-- Staying_ID
CREATE FUNCTION dbo.fn_GenerateNextStayingID()
RETURNS NVARCHAR(20)
AS
BEGIN
    DECLARE @NextID NVARCHAR(20);
    DECLARE @MaxNum INT;

    -- Lấy số lớn nhất từ Room_id (bỏ chữ 'R')
    SELECT @MaxNum = MAX(CAST(SUBSTRING(Staying_id, 3, LEN(Staying_id)) AS INT))
    FROM Staying_Management;

    -- Nếu chưa có dữ liệu thì bắt đầu từ 1
    IF @MaxNum IS NULL
        SET @NextID = 'ST1';
    ELSE
        SET @NextID = 'ST' + CAST(@MaxNum + 1 AS NVARCHAR(20));

    RETURN @NextID;
END;
GO
-- Service_ID
CREATE FUNCTION dbo.fn_GenerateNextServiceID()
RETURNS NVARCHAR(20)
AS
BEGIN
    DECLARE @NextID NVARCHAR(20);
    DECLARE @MaxNum INT;

    -- Lấy số lớn nhất từ Room_id (bỏ chữ 'R')
    SELECT @MaxNum = MAX(CAST(SUBSTRING(Service_id, 2, LEN(Service_id)) AS INT))
    FROM Service_Management;

    -- Nếu chưa có dữ liệu thì bắt đầu từ 1
    IF @MaxNum IS NULL
        SET @NextID = 'S1';
    ELSE
        SET @NextID = 'S' + CAST(@MaxNum + 1 AS NVARCHAR(20));

    RETURN @NextID;
END;
GO
-- Booking_ID
CREATE FUNCTION dbo.fn_GenerateNextBookingID()
RETURNS NVARCHAR(20)
AS
BEGIN
    DECLARE @NextID NVARCHAR(20);
    DECLARE @MaxNum INT;

    -- Lấy số lớn nhất từ Room_id (bỏ chữ 'R')
    SELECT @MaxNum = MAX(CAST(SUBSTRING(Booking_id, 2, LEN(Booking_id)) AS INT))
    FROM Booking_Management;

    -- Nếu chưa có dữ liệu thì bắt đầu từ 1
    IF @MaxNum IS NULL
        SET @NextID = 'B1';
    ELSE
        SET @NextID = 'B' + CAST(@MaxNum + 1 AS NVARCHAR(20));

    RETURN @NextID;
END;
GO
