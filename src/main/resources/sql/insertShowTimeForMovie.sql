DECLARE @StartDate DATE = CAST(GETDATE() AS DATE);
DECLARE @EndDate DATE = DATEADD(DAY, 10, @StartDate);

DECLARE @CurrentDate DATE = @StartDate;

WHILE @CurrentDate <= @EndDate
BEGIN
    DECLARE @MovieID INT = 1;

    WHILE @MovieID <= 23
    BEGIN
        DECLARE @RoomID INT = 1;

        WHILE @RoomID <= 2
        BEGIN
            -- 4 khung gi? m?i ngày
            INSERT INTO ShowTimes (MovieID, RoomID, StartTime, TicketPrice)
            VALUES
            (@MovieID, @RoomID, DATEADD(HOUR, 10, CAST(@CurrentDate AS DATETIME)), 70000),
            (@MovieID, @RoomID, DATEADD(HOUR, 14, CAST(@CurrentDate AS DATETIME)), 90000),
            (@MovieID, @RoomID, DATEADD(HOUR, 18, CAST(@CurrentDate AS DATETIME)), 120000),
            (@MovieID, @RoomID, DATEADD(HOUR, 21, CAST(@CurrentDate AS DATETIME)), 150000);

            SET @RoomID = @RoomID + 1;
        END

        SET @MovieID = @MovieID + 1;
    END

    SET @CurrentDate = DATEADD(DAY, 1, @CurrentDate);
END