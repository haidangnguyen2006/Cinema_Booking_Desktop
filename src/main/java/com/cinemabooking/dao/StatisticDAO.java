package com.cinemabooking.dao;

import com.cinemabooking.model.dto.ChartDataDTO;
import com.cinemabooking.model.dto.KpiDTO;
import com.cinemabooking.connectdb.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {

    // 1. LẤY SỐ LIỆU KPI TỔNG QUAN (UC11)
    public KpiDTO getKPIs(Timestamp fromDate, Timestamp toDate) throws SQLException {
        KpiDTO kpi = new KpiDTO();
        String sql = "SELECT " +
                "ISNULL(SUM(FinalAmount), 0) AS TotalRevenue, " +
                "ISNULL(SUM(DiscountAmount), 0) AS TotalDiscount, " +
                "(SELECT COUNT(TicketID) FROM Tickets t JOIN Invoices i ON t.InvoiceID = i.InvoiceID WHERE i.CreatedDate BETWEEN ? AND ?) AS TotalTickets " +
                "FROM Invoices WHERE CreatedDate BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, fromDate);
            stmt.setTimestamp(2, toDate);
            stmt.setTimestamp(3, fromDate);
            stmt.setTimestamp(4, toDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    kpi.totalRevenue = rs.getDouble("TotalRevenue");
                    kpi.totalDiscount = rs.getDouble("TotalDiscount");
                    kpi.totalTickets = rs.getInt("TotalTickets");
                }
            }
        }
        return kpi;
    }

    // 2. DOANH THU THEO NGÀY
    public List<ChartDataDTO> getRevenueByDay(Timestamp fromDate, Timestamp toDate) throws SQLException {
        List<ChartDataDTO> list = new ArrayList<>();
        String sql = "SELECT CAST(CreatedDate AS DATE) AS DateLabel, SUM(FinalAmount) AS Revenue " +
                "FROM Invoices WHERE CreatedDate BETWEEN ? AND ? " +
                "GROUP BY CAST(CreatedDate AS DATE) ORDER BY DateLabel ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, fromDate);
            stmt.setTimestamp(2, toDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChartDataDTO data = new ChartDataDTO();
                    data.label = rs.getDate("DateLabel").toString();
                    data.value = rs.getDouble("Revenue");
                    list.add(data);
                }
            }
        }
        return list;
    }

    // 3. DOANH THU THEO PHIM (UC12)
    public List<ChartDataDTO> getRevenueByMovie(Timestamp fromDate, Timestamp toDate) throws SQLException {
        List<ChartDataDTO> list = new ArrayList<>();
        String sql = "SELECT m.Title, SUM(t.Price) AS Revenue, COUNT(t.TicketID) AS TicketCount " +
                "FROM Tickets t " +
                "JOIN ShowTimes st ON t.ShowTimeID = st.ShowTimeID " +
                "JOIN Movies m ON st.MovieID = m.MovieID " +
                "JOIN Invoices i ON t.InvoiceID = i.InvoiceID " +
                "WHERE i.CreatedDate BETWEEN ? AND ? AND t.Status = 'Confirmed' " +
                "GROUP BY m.Title ORDER BY Revenue DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, fromDate);
            stmt.setTimestamp(2, toDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChartDataDTO data = new ChartDataDTO();
                    data.label = rs.getString("Title");
                    data.value = rs.getDouble("Revenue");
                    data.quantity = rs.getInt("TicketCount");
                    list.add(data);
                }
            }
        }
        return list;
    }

    // 4. TOP KHÁCH HÀNG ĐÓNG GÓP
    public List<ChartDataDTO> getTopCustomers(Timestamp fromDate, Timestamp toDate) throws SQLException {
        List<ChartDataDTO> list = new ArrayList<>();
        String sql = "SELECT c.FullName, c.Phone, SUM(i.FinalAmount) AS TotalSpent " +
                "FROM Invoices i JOIN Customers c ON i.CustomerPhone = c.Phone " +
                "WHERE i.CreatedDate BETWEEN ? AND ? " +
                "GROUP BY c.FullName, c.Phone ORDER BY TotalSpent DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, fromDate);
            stmt.setTimestamp(2, toDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChartDataDTO data = new ChartDataDTO();
                    data.label = rs.getString("FullName") + " (" + rs.getString("Phone") + ")";
                    data.value = rs.getDouble("TotalSpent");
                    list.add(data);
                }
            }
        }
        return list;
    }
}