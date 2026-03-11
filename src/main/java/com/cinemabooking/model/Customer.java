package com.cinemabooking.model;

public class Customer {
    private String phone;
    private String fullName;
    private int points;

    public Customer() {
    }

    public Customer(String phone, String fullName, int points) {
        this.phone = phone;
        this.fullName = fullName;
        this.points = points;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
