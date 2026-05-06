package com.cinemabooking.dao;

import com.cinemabooking.model.Seat;
import com.cinemabooking.connectdb.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {
    public List<Seat> getSeatsForShowTime(int roomId, int showTimeId) throws SQLException {
        List<Seat> list = new ArrayList<>();
        String sql = "SELECT s.*, " +
                "CASE WHEN t.TicketID IS NOT NULL AND t.Status = 'Confirmed' THEN 1 ELSE 0 END AS IsSold " +
                "FROM Seats s " +
                "LEFT JOIN Tickets t ON s.SeatID = t.SeatID AND t.ShowTimeID = ? " +
                "WHERE s.RoomID = ? " +
                "ORDER BY s.RowChar, s.SeatNumber";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showTimeId);
            stmt.setInt(2, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setSeatId(rs.getInt("SeatID"));
                    seat.setRoomId(rs.getInt("RoomID"));
                    seat.setRowChar(rs.getString("RowChar"));
                    seat.setSeatNumber(rs.getInt("SeatNumber"));
                    seat.setSeatType(rs.getString("SeatType"));
                    seat.setSold(rs.getInt("IsSold") == 1);
                    list.add(seat);
                }
            }
        }
        return list;
    }
}