package com.cinemabooking.dao;

import com.cinemabooking.model.Movie;
import com.cinemabooking.utils.DatabaseConnection;

import java.sql.*;

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
}
