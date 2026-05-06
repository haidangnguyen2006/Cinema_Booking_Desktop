package com.cinemabooking.service;

import com.cinemabooking.dao.SeatDAO;
import com.cinemabooking.dao.ShowTimeDAO;
import com.cinemabooking.model.Seat;
import com.cinemabooking.model.ShowTime;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ShowTimeService {
    private final ShowTimeDAO showTimeDAO;
    private final SeatDAO seatDAO;

    public ShowTimeService() {
        this.showTimeDAO = new ShowTimeDAO();
        this.seatDAO = new SeatDAO();
    }

    // Lấy danh sách lịch chiếu của 1 bộ phim
    public List<ShowTime> getShowTimesForMovie(int movieId) throws SQLException {
        return showTimeDAO.getShowTimesByMovie(movieId);
    }

    // Lấy sơ đồ ghế kèm trạng thái của 1 lịch chiếu
    public List<Seat> getSeatsForShowTime(int roomId, int showTimeId) throws SQLException {
        return seatDAO.getSeatsForShowTime(roomId, showTimeId);
    }
    public List<ShowTime> getShowTimesByMovieAndDate(int movieId, Date date) throws SQLException {
        return showTimeDAO.getShowTimesByMovieAndDate(movieId, date);
    }
}