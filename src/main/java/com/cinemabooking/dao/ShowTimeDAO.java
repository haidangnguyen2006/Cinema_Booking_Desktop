// --- ShowTimeDAO.java ---
package com.cinemabooking.dao;

import com.cinemabooking.model.ShowTime;
import com.cinemabooking.connectdb.DatabaseConnection;
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
    public List<Object[]> getAllShowTimesForTable() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT st.ShowTimeID, m.Title, r.RoomName, st.StartTime, st.TicketPrice " +
                "FROM ShowTimes st " +
                "JOIN Movies m ON st.MovieID = m.MovieID " +
                "JOIN Rooms r ON st.RoomID = r.RoomID " +
                "ORDER BY st.StartTime DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("ShowTimeID"),
                        rs.getString("Title"),
                        rs.getString("RoomName"),
                        rs.getTimestamp("StartTime"),
                        rs.getDouble("TicketPrice")
                });
            }
        }
        return list;
    }

    public boolean isRoomAvailable(int roomId, Timestamp startTime) throws SQLException {
        String sql = "SELECT 1 FROM ShowTimes WHERE RoomID = ? AND ABS(DATEDIFF(MINUTE, StartTime, ?)) < 120";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setTimestamp(2, startTime);
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        }
    }

    // Thêm suất chiếu mới
    public boolean insertShowTime(ShowTime st) throws SQLException {
        String sql = "INSERT INTO ShowTimes (MovieID, RoomID, StartTime, TicketPrice) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, st.getMovieId());
            stmt.setInt(2, st.getRoomId());
            stmt.setTimestamp(3, st.getStartTime());
            stmt.setDouble(4, st.getTicketPrice());
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa suất chiếu
    public boolean deleteShowTime(int showTimeId) throws SQLException {
        String sql = "DELETE FROM ShowTimes WHERE ShowTimeID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showTimeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new SQLException("Không thể xóa suất chiếu này vì đã có vé được bán ra!");
            }
            throw e;
        }
    }
}

