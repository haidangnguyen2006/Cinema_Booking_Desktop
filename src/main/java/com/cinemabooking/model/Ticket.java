package com.cinemabooking.model;

public class Ticket {
    private int ticketId;
    private int invoiceId;
    private int showTimeId;
    private int seatId;
    private double price;
    private String status; // 'Confirmed' hoặc 'Cancelled'

    public Ticket(int ticketId, int invoiceId, int showTimeId, int seatId, double price, String status) {
        this.ticketId = ticketId;
        this.invoiceId = invoiceId;
        this.showTimeId = showTimeId;
        this.seatId = seatId;
        this.price = price;
        this.status = status;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getShowTimeId() {
        return showTimeId;
    }

    public void setShowTimeId(int showTimeId) {
        this.showTimeId = showTimeId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
