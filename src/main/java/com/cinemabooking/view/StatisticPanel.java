package com.cinemabooking.view;

import com.cinemabooking.dao.StatisticDAO;
import com.cinemabooking.model.dto.ChartDataDTO;
import com.cinemabooking.model.dto.KpiDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class StatisticPanel extends JPanel {

    private final StatisticDAO statisticDAO = new StatisticDAO();

    // UI Components
    private JComboBox<String> cbDateFilter;
    private JLabel lblTotalRevenue, lblTotalTickets, lblTotalDiscount;
    private DefaultTableModel modelTab1, modelTab2, modelTab3;

    public StatisticPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Ráp 3 thành phần chính
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        loadStatisticsData();
    }

    // 1. TOP PANEL: BỘ LỌC & NÚT EXCEL
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Báo cáo & Thống kê");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setOpaque(false);

        String[] filters = {"Hôm nay", "7 ngày qua", "Tháng này", "Tất cả thời gian"};
        cbDateFilter = new JComboBox<>(filters);
        cbDateFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbDateFilter.addActionListener(e -> loadStatisticsData());

        filterPanel.add(new JLabel("Lọc theo thời gian: "));
        filterPanel.add(cbDateFilter);
        topPanel.add(filterPanel, BorderLayout.EAST);
        return topPanel;
    }

    // 2. TABBED PANE (3 TAB)
    private JTabbedPane createCenterPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Tab 1: Doanh thu thực thu
        modelTab1 = new DefaultTableModel(new String[]{"Ngày", "Doanh thu thực (VNĐ)"}, 0);
        JTable table1 = new JTable(modelTab1);
        styleTable(table1);
        tabbedPane.addTab("Tổng doanh thu", new JScrollPane(table1));

        // Tab 2: Doanh thu Phim
        modelTab2 = new DefaultTableModel(new String[]{"Tên Phim", "Số vé bán ra", "Tổng tiền vé (VNĐ)"}, 0);
        JTable table2 = new JTable(modelTab2);
        styleTable(table2);
        tabbedPane.addTab("Doanh thu theo Phim", new JScrollPane(table2));

        // Tab 3: Khách hàng
        modelTab3 = new DefaultTableModel(new String[]{"Tên Khách Hàng (SĐT)", "Tổng tiền chi tiêu (VNĐ)"}, 0);
        JTable table3 = new JTable(modelTab3);
        styleTable(table3);
        tabbedPane.addTab("Top Khách hàng", new JScrollPane(table3));

        return tabbedPane;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setEnabled(false);
    }

    // 3. 3 THẺ KPI TỔNG QUAN
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0, 100));

        lblTotalRevenue = new JLabel("0 VNĐ");
        lblTotalTickets = new JLabel("0");
        lblTotalDiscount = new JLabel("0 VNĐ");

        bottomPanel.add(createKpiCard("THỰC THU", lblTotalRevenue, new Color(242, 194, 62))); // Vàng
        bottomPanel.add(createKpiCard("SỐ VÉ ĐÃ BÁN", lblTotalTickets, new Color(52, 152, 219))); // Xanh dương
        bottomPanel.add(createKpiCard("TIỀN GIẢM GIÁ (ĐỔI ĐIỂM)", lblTotalDiscount, new Color(231, 76, 60))); // Đỏ

        return bottomPanel;
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, borderColor),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.DARK_GRAY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // 4. TÍNH TOÁN NGÀY & LOAD DATA
    private void loadStatisticsData() {
        int selectedIndex = cbDateFilter.getSelectedIndex();
        LocalDate today = LocalDate.now();
        LocalDateTime fromDate = null;
        LocalDateTime toDate = today.atTime(LocalTime.MAX);

        switch (selectedIndex) {
            case 0: // Hôm nay
                fromDate = today.atStartOfDay();
                break;
            case 1: // 7 ngày qua
                fromDate = today.minusDays(7).atStartOfDay();
                break;
            case 2: // Tháng này
                fromDate = today.withDayOfMonth(1).atStartOfDay();
                break;
            case 3: // Tất cả
                fromDate = LocalDate.of(2000, 1, 1).atStartOfDay();
                break;
        }

        Timestamp sqlFrom = Timestamp.valueOf(fromDate);
        Timestamp sqlTo = Timestamp.valueOf(toDate);

        new SwingWorker<Void, Void>() {
            KpiDTO kpis;
            List<ChartDataDTO> tab1Data, tab2Data, tab3Data;

            @Override
            protected Void doInBackground() throws Exception {
                kpis = statisticDAO.getKPIs(sqlFrom, sqlTo);
                tab1Data = statisticDAO.getRevenueByDay(sqlFrom, sqlTo);
                tab2Data = statisticDAO.getRevenueByMovie(sqlFrom, sqlTo);
                tab3Data = statisticDAO.getTopCustomers(sqlFrom, sqlTo);
                return null;
            }

            @Override
            protected void done() {
                try {
                    // Update KPI Cards
                    lblTotalRevenue.setText(String.format("%,.0f VNĐ", kpis.totalRevenue));
                    lblTotalTickets.setText(String.format("%,d vé", kpis.totalTickets));
                    lblTotalDiscount.setText(String.format("%,.0f VNĐ", kpis.totalDiscount));

                    modelTab1.setRowCount(0);
                    for (ChartDataDTO d : tab1Data) {
                        modelTab1.addRow(new Object[]{d.label, String.format("%,.0f", d.value)});
                    }

                    modelTab2.setRowCount(0);
                    for (ChartDataDTO d : tab2Data) {
                        modelTab2.addRow(new Object[]{d.label, d.quantity, String.format("%,.0f", d.value)});
                    }

                    modelTab3.setRowCount(0);
                    for (ChartDataDTO d : tab3Data) {
                        modelTab3.addRow(new Object[]{d.label, String.format("%,.0f", d.value)});
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StatisticPanel.this, "Lỗi tải thống kê: " + e.getMessage());
                }
            }
        }.execute();
    }
}