package com.cinemabooking.connectdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String SERVER_NAME = "localhost";
    private static final String DATABASE_NAME = "CinemaBookingDB";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123";
    private static final String PORT = "1433";


    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        String connectionUrl = "jdbc:sqlserver://" + SERVER_NAME + ":" + PORT + ";"
                + "databaseName=" + DATABASE_NAME + ";"
                + "user=" + USERNAME + ";"
                + "password=" + PASSWORD + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;";

        return DriverManager.getConnection(connectionUrl);
    }


}
