package com.cinemabooking.main;

import com.cinemabooking.service.TMDBApiService;
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
        new Thread(() -> {
            try {
                System.out.println("Bắt đầu gọi API kéo phim từ TMDB...");
                TMDBApiService apiService = new TMDBApiService();
                apiService.fetchAndSaveNowPlayingMovies();
            } catch (Exception e) {
                System.err.println("Lỗi khi đồng bộ phim: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);


        });
    }
}
