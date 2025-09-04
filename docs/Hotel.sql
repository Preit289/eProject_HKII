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
    [Booking_id] nvarchar(100) UNIQUE,
    [Checkin_date] datetime NOT NULL,
    [Checkout_date] datetime NOT NULL,
    [Payment_method] nvarchar(50) NOT NULL,
    -- 0 = occupied, 1 = checked out
    [Staying_status] tinyint NOT NULL DEFAULT 0,
    [Total_amount] int NOT NULL DEFAULT 0,
    [Created_at] datetime NOT NULL,
    [Updated_at] datetime NOT NULL,
	[By_role] nvarchar(100) NOT NULL,
    PRIMARY KEY ([Staying_id])
);

CREATE TABLE [dbo].[Staying_Room_Service] (
    [Staying_id] nvarchar(100) NOT NULL,
    [Room_id] NVARCHAR(100) NOT NULL,
    [Service_id] nvarchar(100) NOT NULL,
    [Quantity] INT NOT NULL DEFAULT 0,  -- số lần dùng dịch vụ
    PRIMARY KEY ([Staying_id],[Room_id], [Service_id])
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
    [Room_category] nvarchar(50) NOT NULL,
    [Room_num] nvarchar(100) NOT NULL UNIQUE,
    -- each category room have different quality name
    [Room_quality] nvarchar(50) NOT NULL,
    [Room_price] int NOT NULL,
    -- 0 = Empty, 1 = Occupied, 2 = Cleaning
    [Room_status] tinyint NOT NULL,
    PRIMARY KEY ([Room_id])
);
CREATE TABLE [dbo].[RoomType_Amenity] (
    [Room_category] nvarchar(50) NOT NULL,
    [Room_quality] nvarchar(50) NOT NULL,
	[Room_capacity] int NOT NULL,
    [Room_amenity] nvarchar(300) NOT NULL,
    PRIMARY KEY ([Room_category], [Room_quality])
);

CREATE TABLE [dbo].[Customer_Management] (
    [Customer_id] nvarchar(100) NOT NULL,
    [Customer_name] nvarchar(100) NOT NULL,
    [DOB] date NULL,   -- Có thể NULL nếu trẻ sơ sinh
    [Citizen_identity_number] nvarchar(50) UNIQUE NULL, -- trẻ em thì NULL
    -- 1 = VN, 0 = foreigner
    [Is_foreigner] bit NOT NULL,
    -- 1 = Male, 0 = Female
    [Gender] bit NOT NULL,
	[Is_child] bit NOT NULL DEFAULT 0,
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
    [Planned_checkin_date] datetime NOT NULL,
    [Planned_checkout_date] datetime NOT NULL,
    [Book_by] nvarchar(100) NOT NULL,
    [Book_contact] nvarchar(50) NOT NULL,
    [Created_at] datetime NOT NULL,
    [Updated_at] datetime NOT NULL,
	[By_role] nvarchar(100) NOT NULL,
    PRIMARY KEY ([Booking_id])
);

CREATE TABLE [dbo].[Service_Management] (
    [Service_id] nvarchar(100) NOT NULL,
    [Service_name] nvarchar(100) NOT NULL,
    [Service_price] int NOT NULL,
	[Service_description] nvarchar(100),
    PRIMARY KEY ([Service_id])
);



CREATE TABLE [dbo].[Staying_Room_Customer] (
    [Staying_id] nvarchar(100) NOT NULL,
    [Room_id] nvarchar(100) NOT NULL,
    [Customer_id] nvarchar(100) NOT NULL,
	PRIMARY KEY ([Staying_id], [Room_id],[Customer_id])
);

CREATE TABLE [dbo].[Booking_Room] (
    [Booking_id] nvarchar(100) NOT NULL,
    [Room_id] nvarchar(100) NOT NULL,
    PRIMARY KEY ([Booking_id], [Room_id])
);



-- ================== FOREIGN KEYS ==================
ALTER TABLE [dbo].[Staying_Room_Service]
    ADD CONSTRAINT [FK_SRS_Service] FOREIGN KEY([Service_id]) REFERENCES [dbo].[Service_Management]([Service_id]);
	
ALTER TABLE [dbo].[Staying_Room_Service]
    ADD CONSTRAINT [FK_SRS_Staying] FOREIGN KEY([Staying_id]) REFERENCES [dbo].[Staying_Management]([Staying_id]);

ALTER TABLE [dbo].[Staying_Room_Service]
    ADD CONSTRAINT [FK_SRS_Room] FOREIGN KEY([Room_id]) REFERENCES [dbo].[Room_Management]([Room_id]);

ALTER TABLE [dbo].[Booking_Room]
    ADD CONSTRAINT [FK_BR_Room] FOREIGN KEY([Room_id]) REFERENCES [dbo].[Room_Management]([Room_id]);

ALTER TABLE [dbo].[Booking_Room]
    ADD CONSTRAINT [FK_BR_Booking] FOREIGN KEY([Booking_id]) REFERENCES [dbo].[Booking_Management]([Booking_id]);
	
ALTER TABLE [dbo].[Staying_Room_Customer]
    ADD CONSTRAINT [FK_SRC_Customer] FOREIGN KEY([Customer_id]) REFERENCES [dbo].[Customer_Management]([Customer_id]);
	
ALTER TABLE [dbo].[Staying_Room_Customer]
    ADD CONSTRAINT [FK_SRC_Staying] FOREIGN KEY([Staying_id]) REFERENCES [dbo].[Staying_Management]([Staying_id]);

ALTER TABLE [dbo].[Staying_Room_Customer]
    ADD CONSTRAINT [FK_SRC_Room] FOREIGN KEY([Room_id]) REFERENCES [dbo].[Room_Management]([Room_id]);

ALTER TABLE [dbo].[Staying_Management]
    ADD CONSTRAINT [FK_Staying_Booking] FOREIGN KEY([Booking_id]) REFERENCES [dbo].[Booking_Management]([Booking_id]);
	
ALTER TABLE [dbo].[Room_Management]
	ADD CONSTRAINT [FK_Room_Type] FOREIGN KEY([Room_category], [Room_quality]) REFERENCES [dbo].[RoomType_Amenity]([Room_category], [Room_quality]);

ALTER TABLE [dbo].[Staying_Management]
    ADD CONSTRAINT [FK_Staying_Role] FOREIGN KEY([By_role]) REFERENCES [dbo].[Account_Management]([username]);

ALTER TABLE [dbo].[Booking_Management]
    ADD CONSTRAINT [FK_Booking_Role] FOREIGN KEY([By_role]) REFERENCES [dbo].[Account_Management]([username]);
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

-- Customer_ID
CREATE FUNCTION dbo.fn_GenerateNextCustomerID()
RETURNS NVARCHAR(20)
AS
BEGIN
    DECLARE @NextID NVARCHAR(20);
    DECLARE @MaxNum INT;

    -- Lấy số lớn nhất từ Customer_id (bỏ chữ 'C')
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

    -- Lấy số lớn nhất từ Staying_id (bỏ chữ 'ST')
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

    -- Lấy số lớn nhất từ Service_id (bỏ chữ 'S')
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

    -- Lấy số lớn nhất từ Booking_id (bỏ chữ 'B')
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
