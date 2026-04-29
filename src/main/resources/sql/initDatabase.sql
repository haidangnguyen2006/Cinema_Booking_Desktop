-- =========================================================
-- INIT DATABASE SCRIPT CHO DỰ ÁN CINEMA BOOKING
-- =========================================================

-- 1. TẠO DATABASE
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'CinemaBookingDB')
BEGIN
    CREATE DATABASE CinemaBookingDB;
END
GO

USE CinemaBookingDB;
GO

-- =========================================================
-- 2. XÓA CÁC BẢNG CŨ NẾU TỒN TẠI (Theo thứ tự đảo ngược của Khóa Ngoại)
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
-- 3. TẠO CẤU TRÚC CÁC BẢNG (DDL)
-- =========================================================
PRINT N'Đang tạo cấu trúc bảng mới...';

-- Bảng Nhân viên / Quản lý
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    Role VARCHAR(20) NOT NULL -- 'Admin' hoặc 'Staff'
);

-- Bảng Khách hàng thành viên
CREATE TABLE Customers (
    Phone VARCHAR(15) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    Points INT DEFAULT 0
);

-- Bảng Phim
CREATE TABLE Movies (
    MovieID INT IDENTITY(1,1) PRIMARY KEY,
    TmdbId INT,
    Title NVARCHAR(255) NOT NULL,
    ReleaseDate DATE,
    Duration INT,
    Genre NVARCHAR(100),
    Rating DECIMAL(3,1),
    PosterUrl VARCHAR(500)
);

-- Bảng Phòng chiếu
CREATE TABLE Rooms (
    RoomID INT IDENTITY(1,1) PRIMARY KEY,
    RoomName NVARCHAR(50) NOT NULL,
    TotalCapacity INT NOT NULL
);

-- Bảng Ghế ngồi
CREATE TABLE Seats (
    SeatID INT IDENTITY(1,1) PRIMARY KEY,
    RoomID INT NOT NULL FOREIGN KEY REFERENCES Rooms(RoomID),
    RowChar VARCHAR(1) NOT NULL,
    SeatNumber INT NOT NULL,
    SeatType VARCHAR(20) DEFAULT 'Standard' -- 'Standard', 'VIP'
);

-- Bảng Lịch chiếu (Suất chiếu)
CREATE TABLE ShowTimes (
    ShowTimeID INT IDENTITY(1,1) PRIMARY KEY,
    MovieID INT NOT NULL FOREIGN KEY REFERENCES Movies(MovieID),
    RoomID INT NOT NULL FOREIGN KEY REFERENCES Rooms(RoomID),
    StartTime DATETIME NOT NULL,
    TicketPrice DECIMAL(10,2) NOT NULL
);

-- Bảng Hóa đơn
CREATE TABLE Invoices (
    InvoiceID INT IDENTITY(1,1) PRIMARY KEY,
    StaffID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    CustomerPhone VARCHAR(15) NULL FOREIGN KEY REFERENCES Customers(Phone),
    TotalAmount DECIMAL(10,2) NOT NULL,
    DiscountAmount DECIMAL(10,2) DEFAULT 0,
    FinalAmount DECIMAL(10,2) NOT NULL,
    EarnedPoints INT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Vé (Chi tiết hóa đơn)
CREATE TABLE Tickets (
    TicketID INT IDENTITY(1,1) PRIMARY KEY,
    InvoiceID INT NULL FOREIGN KEY REFERENCES Invoices(InvoiceID),
    ShowTimeID INT NOT NULL FOREIGN KEY REFERENCES ShowTimes(ShowTimeID),
    SeatID INT NOT NULL FOREIGN KEY REFERENCES Seats(SeatID),
    Price DECIMAL(10,2) NOT NULL,
    Status VARCHAR(20) DEFAULT 'Confirmed' -- 'Confirmed', 'Cancelled'
);
GO

-- =========================================================
-- 4. INSERT DỮ LIỆU MẪU (MOCK DATA) ĐỂ TEST APP
-- =========================================================
PRINT N'Đang khởi tạo dữ liệu mẫu...';

-- 4.1. Tài khoản Test
INSERT INTO Users (Username, Password, FullName, Role) VALUES
('admin', 'admin', N'Quản trị viên hệ thống', 'Admin'),
('staff01', 'staff01', N'Nguyễn Văn Thu Ngân', 'Staff');

-- 4.2. Khách hàng Test
INSERT INTO Customers (Phone, FullName, Points) VALUES
('0987654321', N'Trần Khách Hàng', 250), -- Khách có sẵn > 200 điểm để test giảm giá
('0123456789', N'Lê Người Dùng', 50);

-- 4.3. Phim Test (Dùng tạm trước khi Sync từ TMDB)
INSERT INTO Movies (TmdbId, Title, ReleaseDate, Duration, Genre, Rating, PosterUrl) VALUES
(101, N'Lật Mặt 7: Một Điều Ước', '2024-04-26', 120, N'Tâm Lý, Gia Đình', 8.5, 'https://image.tmdb.org/t/p/w500/1E5baAaEse26fej7uHcjOgEE2t2.jpg'),
(102, N'Godzilla x Kong: Đế Chế Mới', '2024-03-29', 115, N'Hành Động, Viễn Tưởng', 7.8, 'https://image.tmdb.org/t/p/w500/tMefBSflR6PGQLvLuPEHZot1pFi.jpg'),
(103, N'Kung Fu Panda 4', '2024-03-08', 94, N'Hoạt Hình, Hài Hước', 8.0, 'https://image.tmdb.org/t/p/w500/kDp1vUBnMpe8ak4rjgl3cLELqjU.jpg');

-- 4.4. Phòng chiếu Test
INSERT INTO Rooms (RoomName, TotalCapacity) VALUES
('Cinema 1', 70),
('Cinema 2', 70),
('Cinema 3 (VIP)', 70);

-- 4.5. Tự động sinh 70 Ghế (Hàng A-G, Cột 1-10) cho tất cả các phòng chiếu
INSERT INTO Seats (RoomID, RowChar, SeatNumber, SeatType)
SELECT
    r.RoomID,
    Rows.RowChar,
    Cols.SeatNumber,
    CASE WHEN Rows.RowChar IN ('F', 'G') THEN 'VIP' ELSE 'Standard' END AS SeatType
FROM Rooms r
CROSS JOIN (VALUES ('A'), ('B'), ('C'), ('D'), ('E'), ('F'), ('G')) AS Rows(RowChar)
CROSS JOIN (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10)) AS Cols(SeatNumber);

-- 4.6. Tự động sinh Lịch chiếu (Từ hôm nay đến 2 ngày sau)
DECLARE @MovieID INT;
DECLARE movie_cursor CURSOR FOR SELECT MovieID FROM Movies;
OPEN movie_cursor;
FETCH NEXT FROM movie_cursor INTO @MovieID;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Lặp qua 3 ngày (Hôm nay, Ngày mai, Mốt) để app có dữ liệu test chuyển ngày
    DECLARE @DayOffset INT = 0;
    WHILE @DayOffset <= 2
    BEGIN
        -- Random 2 đến 5 suất chiếu mỗi phim / mỗi ngày
        DECLARE @TotalShows INT = CAST(RAND() * 4 + 2 AS INT);
        DECLARE @Counter INT = 1;

        WHILE @Counter <= @TotalShows
        BEGIN
            -- Random phòng chiếu (1, 2 hoặc 3)
            DECLARE @RandomRoomID INT = CAST(RAND() * 3 + 1 AS INT);

            -- Random Giờ chiếu (từ 08:00 đến 22:00) và Phút (00, 15, 30, 45)
            DECLARE @RandomHour INT = CAST(RAND() * 15 + 8 AS INT);
            DECLARE @RandomMinute INT = CAST(RAND() * 4 AS INT) * 15;

            -- Tính thời gian bắt đầu
            DECLARE @StartTime DATETIME = CAST(CAST(GETDATE() + @DayOffset AS DATE) AS DATETIME);
            SET @StartTime = DATEADD(HOUR, @RandomHour, @StartTime);
            SET @StartTime = DATEADD(MINUTE, @RandomMinute, @StartTime);

            -- Random Giá vé (75.000, 85.000, 95.000)
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

PRINT N'✅ HOÀN TẤT! Toàn bộ Database đã sẵn sàng hoạt động.';
GO