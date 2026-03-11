package com.cinemabooking.model;

public class Invoice {
    private int invoiceId;
    private String staffId;
    private String customerPhone; // nullable
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private int earnedPoints;

    public Invoice() {
        this.staffId = staffId;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.earnedPoints = earnedPoints;
    }

    public Invoice(int invoiceId, String staffId, String customerPhone, double totalAmount, double discountAmount, double finalAmount, int earnedPoints) {
        this.invoiceId = invoiceId;
        this.staffId = staffId;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.earnedPoints = earnedPoints;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(int earnedPoints) {
        this.earnedPoints = earnedPoints;
    }
}
