// --- Seat.java ---
package com.cinemabooking.model;

public class Seat {
    private int seatId;
    private Room room;
    private String rowChar;
    private int seatNumber;
    private String seatType;
    private boolean isSold;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    public String getRowChar() { return rowChar; }
    public void setRowChar(String rowChar) { this.rowChar = rowChar; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public boolean isSold() { return isSold; }
    public void setSold(boolean sold) { isSold = sold; }
    public String getSeatName() { return rowChar + seatNumber; } // Trả về "A1", "B2"...
}