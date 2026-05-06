package com.cinemabooking.service;

import com.cinemabooking.dao.CustomerDAO;
import com.cinemabooking.model.Customer;

import java.sql.SQLException;

public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    // Tra cứu khách hàng
    public Customer findCustomerByPhone(String phone) throws SQLException {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        return customerDAO.getCustomerByPhone(phone.trim());
    }

    // Đăng ký khách hàng mới
    public boolean registerNewCustomer(String phone, String fullName) throws SQLException {
        if (phone == null || phone.trim().isEmpty() || fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ Số điện thoại và Họ tên.");
        }

        if (!phone.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (Phải bắt đầu bằng 0 và có 10 số).");
        }

        Customer newCustomer = new Customer();
        newCustomer.setPhone(phone.trim());
        newCustomer.setFullName(fullName.trim());

        return customerDAO.insertCustomer(newCustomer);
    }
}
