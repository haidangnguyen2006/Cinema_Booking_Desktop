package com.cinemabooking.main;

import com.cinemabooking.utils.DatabaseConnection;
import com.cinemabooking.view.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Không thể khởi tạo giao diện FlatLaf.");
        }
        // Test connection
        DatabaseConnection.getConnection();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);


        });
    }
}
