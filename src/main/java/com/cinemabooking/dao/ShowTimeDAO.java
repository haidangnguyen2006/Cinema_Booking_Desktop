// --- ShowTimeDAO.java ---
package com.cinemabooking.dao;

import com.cinemabooking.model.ShowTime;
import com.cinemabooking.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowTimeDAO {
    public List<ShowTime> getShowTimesByMovie(int movieId) throws SQLException {
        List<ShowTime> list = new ArrayList<>();
        String sql = "SELECT * FROM ShowTimes WHERE MovieID = ? ORDER BY StartTime ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ShowTime st = new ShowTime();
                    st.setShowTimeId(rs.getInt("ShowTimeID"));
                    st.setMovieId(rs.getInt("MovieID"));
                    st.setRoomId(rs.getInt("RoomID"));
                    st.setStartTime(rs.getTimestamp("StartTime"));
                    st.setTicketPrice(rs.getDouble("TicketPrice"));
                    list.add(st);
                }
            }
        }
        return list;
    }
    public List<ShowTime> getShowTimesByMovieAndDate(int movieId, java.sql.Date date) throws SQLException {
        List<ShowTime> list = new ArrayList<>();
        String sql = "SELECT * FROM ShowTimes WHERE MovieID = ? AND CAST(StartTime AS DATE) = ? ORDER BY StartTime ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setDate(2, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ShowTime st = new ShowTime();
                    st.setShowTimeId(rs.getInt("ShowTimeID"));
                    st.setMovieId(rs.getInt("MovieID"));
                    st.setRoomId(rs.getInt("RoomID"));
                    st.setStartTime(rs.getTimestamp("StartTime"));
                    st.setTicketPrice(rs.getDouble("TicketPrice"));
                    list.add(st);
                }
            }
        }
        return list;
    }
    
}

