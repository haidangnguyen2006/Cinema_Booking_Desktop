package com.cinemabooking.model;

public class Ticket {
    private int ticketId;
    private Invoice invoice;
    private ShowTime showTime;
    private Seat seat;
    private double price;
    private String status; // 'Confirmed', 'Cancelled'

    public Ticket() {
    }
    public Ticket(int ticketId, Invoice invoice, ShowTime showTime, Seat seat, double price, String status) {
        this.ticketId = ticketId;
        this.invoice = invoice;
        this.showTime = showTime;
        this.seat = seat;
        this.price = price;
        this.status = status;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public void setShowTime(ShowTime showTime) {
        this.showTime = showTime;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
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
