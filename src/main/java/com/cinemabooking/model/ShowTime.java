// --- ShowTime.java ---
package com.cinemabooking.model;
import java.sql.Timestamp;

public class ShowTime {
    private int showTimeId;
    private Movie movie;
    private Room room;
    private Timestamp startTime;
    private double ticketPrice;

    public int getShowTimeId() {
        return showTimeId;
    }

    public void setShowTimeId(int showTimeId) {
        this.showTimeId = showTimeId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}

