package com.cinemabooking.dao;

import com.cinemabooking.model.Movie;
import com.cinemabooking.connectdb.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    public boolean isMovieExist(int tmdbId) throws SQLException {
        String sql = "SELECT 1 FROM Movies WHERE TMDB_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tmdbId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Nếu có kết quả trả về true
            }
        }
    }

    // 2. Thêm phim mới vào CSDL
    public boolean insertMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO Movies (TMDB_ID, Title, Description, ReleaseDate, Duration, PosterURL, Genre, Rating) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movie.getTmdbId());
            stmt.setString(2, movie.getTitle());
            stmt.setString(3, movie.getDescription());
            if (movie.getReleaseDate() != null) {
                stmt.setDate(4, new java.sql.Date(movie.getReleaseDate().getTime()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setInt(5, movie.getDuration());
            stmt.setString(6, movie.getPosterUrl());
            stmt.setString(7, movie.getGenre());
            stmt.setDouble(8, movie.getRating());

            return stmt.executeUpdate() > 0;
        }
    }
    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movies";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setMovieId(rs.getInt("MovieID"));
                movie.setTmdbId(rs.getInt("TMDB_ID"));
                movie.setTitle(rs.getString("Title"));
                Date sqlDate = rs.getDate("ReleaseDate");
                if (sqlDate != null) {
                    movie.setReleaseDate(new java.util.Date(sqlDate.getTime()));
                }
                movie.setDuration(rs.getInt("Duration"));
                movie.setPosterUrl(rs.getString("PosterURL"));
                movie.setGenre(rs.getString("Genre"));
                movie.setRating(rs.getDouble("Rating"));

                movies.add(movie);
            }
        }
        return movies;
    }
    public boolean deleteMovie(int movieId) throws SQLException {
        String sql = "DELETE FROM Movies WHERE MovieID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 547) { // 547 là mã lỗi vi phạm ràng buộc khóa ngoại trong SQL Server
                throw new SQLException("Không thể xóa phim này vì đang có Lịch chiếu hoặc Vé đã được bán!");
            }
            throw e;
        }
    }
}
