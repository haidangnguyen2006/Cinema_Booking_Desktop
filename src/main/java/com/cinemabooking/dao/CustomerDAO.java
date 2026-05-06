package com.cinemabooking.dao;

import com.cinemabooking.model.Customer;
import com.cinemabooking.connectdb.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {
    public Customer getCustomerByPhone(String phone) throws SQLException {
        String sql = "SELECT Phone, FullName, Points FROM Customers WHERE Phone = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setPhone(rs.getString("Phone"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setPoints(rs.getInt("Points"));
                    return customer;
                }
            }
        }
        return null;
    }


    public boolean insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customers (Phone, FullName) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getPhone());
            stmt.setString(2, customer.getFullName());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627) {
                throw new SQLException("Số điện thoại này đã được đăng ký!");
            }
            throw e;
        }
    }
}
