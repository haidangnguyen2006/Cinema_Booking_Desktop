package com.cinemabooking.model;

import com.cinemabooking.enums.Role;

public class User {
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private Role role;

    public User() {}

    public User(String userId, String username, String password, String fullName, String role) {
        setUsername(username);
        setUserId(userId);
        setPassword(password);
        setFullName(fullName);
        setRole(role);
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(String role) {
        this.role = Role.valueOf(role.toUpperCase());
    }
}
