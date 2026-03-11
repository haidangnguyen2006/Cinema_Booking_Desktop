package com.cinemabooking.service;

import com.cinemabooking.dao.UserDAO;
import com.cinemabooking.model.User;
import com.cinemabooking.utils.SessionManager;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public boolean login(String username, String password) throws SQLException {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            SessionManager.setCurrentUser(user); // Lưu phiên đăng nhập
            return true;
        }
        return false;
    }
    public boolean register(String username, String password, String fullname,String role) throws SQLException{
        User user = userDAO.register(username, password,fullname,role);
        if (user != null) {
//            SessionManager.setCurrentUser(user); // Lưu phiên đăng nhập
            return true;
        }
        return false;
    }
}
