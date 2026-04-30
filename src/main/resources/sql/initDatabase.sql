-- =========================================================
-- INIT DATABASE SCRIPT CHO DỰ ÁN CINEMA BOOKING (CHUẨN ERD)
-- =========================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'CinemaBookingDB')
BEGIN
    CREATE DATABASE CinemaBookingDB;
END
GO

USE CinemaBookingDB;
GO

-- =========================================================
-- 1. XÓA CÁC BẢNG CŨ NẾU TỒN TẠI (Thứ tự xóa phải từ con -> cha)
-- =========================================================
PRINT N'Đang dọn dẹp các bảng cũ...';
IF OBJECT_ID('Tickets', 'U') IS NOT NULL DROP TABLE Tickets;
IF OBJECT_ID('Invoices', 'U') IS NOT NULL DROP TABLE Invoices;
IF OBJECT_ID('ShowTimes', 'U') IS NOT NULL DROP TABLE ShowTimes;
IF OBJECT_ID('Seats', 'U') IS NOT NULL DROP TABLE Seats;
IF OBJECT_ID('Rooms', 'U') IS NOT NULL DROP TABLE Rooms;
IF OBJECT_ID('Movies', 'U') IS NOT NULL DROP TABLE Movies;
IF OBJECT_ID('Customers', 'U') IS NOT NULL DROP TABLE Customers;
IF OBJECT_ID('Users', 'U') IS NOT NULL DROP TABLE Users;
GO

-- =========================================================
-- 2. TẠO CẤU TRÚC CÁC BẢNG (DDL)
-- =========================================================
PRINT N'Đang tạo cấu trúc bảng mới theo chuẩn ERD...';

-- Bảng Users (Đã thêm IsActive và CreatedAt)
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    Role VARCHAR(20) NOT NULL,
    IsActive BIT DEFAULT 1, -- 1: Đang làm, 0: Đã nghỉ
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Customers (Đã thêm CreatedAt)
CREATE TABLE Customers (
    Phone VARCHAR(15) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    Points INT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Movies (Đã thêm Description, đổi TMDB_ID, PosterURL)
CREATE TABLE Movies (
    MovieID INT IDENTITY(1,1) PRIMARY KEY,
    TMDB_ID INT,
    Title NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX),
    ReleaseDate DATE,
    Duration INT,
    PosterURL VARCHAR(500),
    Genre NVARCHAR(100),
    Rating DECIMAL(3,1)
);

-- Bảng Rooms (Chuẩn ERD)
CREATE TABLE Rooms (
    RoomID INT IDENTITY(1,1) PRIMARY KEY,
    RoomName NVARCHAR(50) NOT NULL,
    TotalCapacity INT NOT NULL
);

-- Bảng Seats (Chuẩn ERD)
CREATE TABLE Seats (
    SeatID INT IDENTITY(1,1) PRIMARY KEY,
    RoomID INT NOT NULL FOREIGN KEY REFERENCES Rooms(RoomID),
    RowChar VARCHAR(1) NOT NULL,
    SeatNumber INT NOT NULL,
    SeatType VARCHAR(20) DEFAULT 'Standard'
);

-- Bảng ShowTimes (Chuẩn ERD)
CREATE TABLE ShowTimes (
    ShowTimeID INT IDENTITY(1,1) PRIMARY KEY,
    MovieID INT NOT NULL FOREIGN KEY REFERENCES Movies(MovieID),
    RoomID INT NOT NULL FOREIGN KEY REFERENCES Rooms(RoomID),
    StartTime DATETIME NOT NULL,
    TicketPrice DECIMAL(10,2) NOT NULL
);

-- Bảng Invoices (Đã sửa CreatedAt thành CreatedDate theo ERD)
CREATE TABLE Invoices (
    InvoiceID INT IDENTITY(1,1) PRIMARY KEY,
    StaffID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    CustomerPhone VARCHAR(15) NULL FOREIGN KEY REFERENCES Customers(Phone),
    TotalAmount DECIMAL(10,2) NOT NULL,
    DiscountAmount DECIMAL(10,2) DEFAULT 0,
    FinalAmount DECIMAL(10,2) NOT NULL,
    EarnedPoints INT DEFAULT 0,
    CreatedDate DATETIME DEFAULT GETDATE()
);

-- Bảng Tickets (Chuẩn ERD)
CREATE TABLE Tickets (
    TicketID INT IDENTITY(1,1) PRIMARY KEY,
    InvoiceID INT NULL FOREIGN KEY REFERENCES Invoices(InvoiceID),
    ShowTimeID INT NOT NULL FOREIGN KEY REFERENCES ShowTimes(ShowTimeID),
    SeatID INT NOT NULL FOREIGN KEY REFERENCES Seats(SeatID),
    Price DECIMAL(10,2) NOT NULL,
    Status VARCHAR(20) DEFAULT 'Confirmed'
);
GO

-- =========================================================
-- 3. INSERT DỮ LIỆU MẪU ĐỂ TEST APP
-- =========================================================
PRINT N'Đang khởi tạo dữ liệu mẫu...';

-- Users
INSERT INTO Users (Username, Password, FullName, Role, IsActive) VALUES 
('admin', 'admin', N'Quản trị viên', 'Admin', 1),
('staff01', 'staff01', N'Nguyễn Văn Thu Ngân', 'Staff', 1);

-- Customers
INSERT INTO Customers (Phone, FullName, Points) VALUES 
('0987654321', N'Trần Khách Hàng', 250),
('0123456789', N'Lê Người Dùng', 50);

-- Movies (Đã chèn thêm Description mẫu)
INSERT INTO Movies (TMDB_ID, Title, Description, ReleaseDate, Duration, Genre, Rating, PosterURL) VALUES 
(101, N'Lật Mặt 7: Một Điều Ước', N'Một câu chuyện cảm động về tình mẫu tử và gia đình.', '2024-04-26', 120, N'Tâm Lý, Gia Đình', 8.5, 'https://image.tmdb.org/t/p/w500/1E5baAaEse26fej7uHcjOgEE2t2.jpg'),
(102, N'Godzilla x Kong: Đế Chế Mới', N'Cuộc chiến giữa hai siêu quái thú vĩ đại nhất hành tinh.', '2024-03-29', 115, N'Hành Động, Viễn Tưởng', 7.8, 'https://image.tmdb.org/t/p/w500/tMefBSflR6PGQLvLuPEHZot1pFi.jpg'),
(103, N'Kung Fu Panda 4', N'Hành trình mới của chú gấu trúc Po dũng cảm.', '2024-03-08', 94, N'Hoạt Hình, Hài Hước', 8.0, 'https://image.tmdb.org/t/p/w500/kDp1vUBnMpe8ak4rjgl3cLELqjU.jpg');

-- Rooms
INSERT INTO Rooms (RoomName, TotalCapacity) VALUES 
('Cinema 1', 70), ('Cinema 2', 70), ('Cinema 3 (VIP)', 70);

-- Seats (Sinh tự động A-G, 1-10)
INSERT INTO Seats (RoomID, RowChar, SeatNumber, SeatType)
SELECT r.RoomID, Rows.RowChar, Cols.SeatNumber, CASE WHEN Rows.RowChar IN ('F', 'G') THEN 'VIP' ELSE 'Standard' END AS SeatType
FROM Rooms r
CROSS JOIN (VALUES ('A'), ('B'), ('C'), ('D'), ('E'), ('F'), ('G')) AS Rows(RowChar)
CROSS JOIN (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10)) AS Cols(SeatNumber);

-- ShowTimes (Sinh lịch chiếu 3 ngày)
DECLARE @MovieID INT;
DECLARE movie_cursor CURSOR FOR SELECT MovieID FROM Movies;
OPEN movie_cursor;
FETCH NEXT FROM movie_cursor INTO @MovieID;

WHILE @@FETCH_STATUS = 0
BEGIN
    DECLARE @DayOffset INT = 0;
    WHILE @DayOffset <= 2 
    BEGIN
        DECLARE @TotalShows INT = CAST(RAND() * 4 + 2 AS INT); 
        DECLARE @Counter INT = 1;

        WHILE @Counter <= @TotalShows
        BEGIN
            DECLARE @RandomRoomID INT = CAST(RAND() * 3 + 1 AS INT);
            DECLARE @RandomHour INT = CAST(RAND() * 15 + 8 AS INT);
            DECLARE @RandomMinute INT = CAST(RAND() * 4 AS INT) * 15;
            DECLARE @StartTime DATETIME = CAST(CAST(GETDATE() + @DayOffset AS DATE) AS DATETIME);
            SET @StartTime = DATEADD(HOUR, @RandomHour, @StartTime);
            SET @StartTime = DATEADD(MINUTE, @RandomMinute, @StartTime);
            DECLARE @RandomPrice DECIMAL(10,2) = CASE CAST(RAND() * 3 AS INT) WHEN 0 THEN 75000 WHEN 1 THEN 85000 ELSE 95000 END;

            INSERT INTO ShowTimes (MovieID, RoomID, StartTime, TicketPrice)
            VALUES (@MovieID, @RandomRoomID, @StartTime, @RandomPrice);

            SET @Counter = @Counter + 1;
        END
        SET @DayOffset = @DayOffset + 1;
    END
    FETCH NEXT FROM movie_cursor INTO @MovieID;
END
CLOSE movie_cursor;
DEALLOCATE movie_cursor;

PRINT N'✅ HOÀN TẤT! DB ĐÃ CHUẨN ERD 100%.';
GO