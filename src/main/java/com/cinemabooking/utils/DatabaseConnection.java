package com.cinemabooking.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String SERVER_NAME = "localhost";
    private static final String DATABASE_NAME = "CinemaBookingDB";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123";
    private static final String PORT = "1433";

    private static Connection connection = null;

    // (Singleton)
    private DatabaseConnection() { }

    // Phương thức lấy Connection
    public static Connection getConnection() {
        try {
            // Kiểm tra nếu connection chưa được tạo hoặc đã bị đóng
            if (connection == null || connection.isClosed()) {
                String connectionUrl = "jdbc:sqlserver://" + SERVER_NAME + ":" + PORT + ";"
                        + "databaseName=" + DATABASE_NAME + ";"
                        + "user=" + USERNAME + ";"
                        + "password=" + PASSWORD + ";"
                        + "trustServerCertificate=true;";

                connection = DriverManager.getConnection(connectionUrl);
                System.out.println("Kết nối SQL Server thành công!");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối cơ sở dữ liệu.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
