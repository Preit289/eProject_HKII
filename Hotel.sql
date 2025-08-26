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

