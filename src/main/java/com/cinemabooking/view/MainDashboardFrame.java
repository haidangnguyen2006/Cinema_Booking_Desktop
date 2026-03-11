package com.cinemabooking.view;

import com.cinemabooking.utils.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDashboardFrame extends JFrame {

    // Theme
    private final Color COLOR_DARK_NAV = new Color(30, 30, 30);
    private final Color COLOR_BG_LIGHT = new Color(245, 246, 250);
    private final Color COLOR_PRIMARY_YELLOW = new Color(242, 194, 62);
    private final Color COLOR_LIGHT_YELLOW = new Color(250, 233, 165);

    public MainDashboardFrame() {
        setTitle("Cinema POS System - Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {

        // 1. Thêm Header (Thanh điều hướng bên trên)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Thêm Khu vực chính (Content bên dưới)
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    // ==========================================
    // KHU VỰC HEADER (NAVBAR)
    // ==========================================
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_DARK_NAV);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        // --- Cụm Logo & Menu (Bên trái) ---
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        leftNav.setOpaque(false);
        ImageIcon iuhLogo=new ImageIcon(getClass().getResource("/icons/iuh-logo.png"));
        Image imgIuh = iuhLogo.getImage();
        Image newImgIuh = imgIuh.getScaledInstance(48, 48, Image.SCALE_SMOOTH);

        ImageIcon scaledIconIuh = new ImageIcon(newImgIuh);

        JLabel lblIcon = new JLabel(scaledIconIuh, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));

        JLabel lblLogo = new JLabel("CINEMA");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);

        leftNav.add(lblIcon);
        leftNav.add(lblLogo);

        // Các nút Menu
        String[] menus = {"Bán vé", "Phim", "Lịch chiếu", "Thống kê", "Hỗ trợ"};
        for (String menu : menus) {
            JButton btnMenu = new JButton(menu);
            btnMenu.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            btnMenu.setForeground(new Color(200, 200, 200));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setFocusPainted(false);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Hiệu ứng hover
            btnMenu.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btnMenu.setForeground(COLOR_PRIMARY_YELLOW); }
                public void mouseExited(MouseEvent e) { btnMenu.setForeground(new Color(200, 200, 200)); }
            });
            leftNav.add(btnMenu);
        }

        // --- Cụm Thông tin User (Bên phải) ---
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rightNav.setOpaque(false);

        // Lấy tên nhân viên từ SessionManager
        String staffName = SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getFullName() : "Admin";
        JLabel lblUser = new JLabel("Xin chào, " + staffName);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUser.setForeground(Color.WHITE);

        // Icon Demo (Bạn có thể thay bằng ImageIcon thực tế sau)
        ImageIcon userIcon=new ImageIcon(getClass().getResource("/icons/person-48.png"));
        Image img = userIcon.getImage();
        Image newImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);

        ImageIcon scaledIcon = new ImageIcon(newImg);

        JLabel lblUserIcon = new JLabel(scaledIcon);
        lblUserIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblUserIcon.setForeground(Color.WHITE);

        rightNav.add(lblUser);
        rightNav.add(lblUserIcon);

        headerPanel.add(leftNav, BorderLayout.WEST);
        headerPanel.add(rightNav, BorderLayout.EAST);

        return headerPanel;
    }


    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG_LIGHT);
        mainPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        // --- Dòng chào mừng (Top) ---
        JPanel greetingPanel = new JPanel();
        greetingPanel.setLayout(new BoxLayout(greetingPanel, BoxLayout.Y_AXIS));
        greetingPanel.setOpaque(false);

        String staffName = SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getFullName() : "Tên nhân viên";
        JLabel lblWelcome = new JLabel("Xin chào, " + staffName);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel lblSub = new JLabel("Chào mừng bạn trở lại hệ thống quản lý rạp chiếu phim!");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(Color.GRAY);

        greetingPanel.add(lblWelcome);
        greetingPanel.add(Box.createVerticalStrut(5));
        greetingPanel.add(lblSub);
        greetingPanel.add(Box.createVerticalStrut(40)); // Khoảng cách tới các thẻ

        mainPanel.add(greetingPanel, BorderLayout.NORTH);

        // --- Lưới các thẻ chức năng (Center) ---
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 40, 40));
        gridPanel.setOpaque(false);

        // Tạo các thẻ (Sử dụng class DashboardCard tự custom bên dưới)
        DashboardCard cardBanVe = new DashboardCard("Bán vé",
                "Quản lý và bán vé cho khách hàng", new ImageIcon(getClass().getResource("/icons/cinema-ticket-100.png")));
        DashboardCard cardPhim = new DashboardCard("Phim",
                "Quản lý và thông tin phim", new ImageIcon(getClass().getResource("/icons/film-96.png")));
        DashboardCard cardLichChieu = new DashboardCard("Lịch chiếu",
                "Sắp xếp và quản lý lịch chiếu", new ImageIcon(getClass().getResource("/icons/calendar-100.png")));
        DashboardCard cardThongKe = new DashboardCard("Thống kê",
                "Thống kê và xuất báo cáo", new ImageIcon(getClass().getResource("/icons/statistic-96.png")));

        // Gắn sự kiện click (Ví dụ click vào Bán vé)
        cardBanVe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(MainDashboardFrame.this, "Mở màn hình POS Bán vé...");
                // TODO: Chuyển sang panel Bán vé
            }
        });

        gridPanel.add(cardBanVe);
        gridPanel.add(cardPhim);
        gridPanel.add(cardLichChieu);
        gridPanel.add(cardThongKe);

        // Bọc gridPanel vào một Panel khác để giới hạn chiều cao (không bị giãn quá đà)
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerWrapper.setOpaque(false);
        gridPanel.setPreferredSize(new Dimension(1000, 400));
        centerWrapper.add(gridPanel);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        return mainPanel;
    }

    // ==========================================
    // CUSTOM COMPONENT: THẺ CHỨC NĂNG (DASHBOARD CARD)
    // ==========================================
    class DashboardCard extends JPanel {
        private String title;
        private String description;
        private ImageIcon iconSymbol;

        public DashboardCard(String title, String description, ImageIcon iconSymbol) {
            this.title = title;
            this.description = description;
            this.iconSymbol = iconSymbol;

            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setLayout(new GridBagLayout());

            // Hiệu ứng hover (Phóng to/Bóng đổ nhẹ - ở đây đơn giản là đổi màu viền)
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY_YELLOW, 2)); }
                public void mouseExited(MouseEvent e) { setBorder(null); }
            });

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.BOTH;

            // Icon
            Image img = iconSymbol.getImage();
            Image newImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);

            ImageIcon scaledIcon = new ImageIcon(newImg);

            JLabel lblIcon = new JLabel(scaledIcon, SwingConstants.CENTER);
            lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridheight = 2;
            gbc.weightx = 0.2;
            add(lblIcon, gbc);

            // Title
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
            gbc.gridx = 1; gbc.gridy = 0;
            gbc.gridheight = 1;
            gbc.weightx = 0.8;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            add(lblTitle, gbc);

            // Description
            JLabel lblDesc = new JLabel(description);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblDesc.setForeground(Color.GRAY);
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            add(lblDesc, gbc);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Vẽ nền trắng bo góc
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            // 2. Vẽ họa tiết tròn màu vàng ở góc trên bên phải (Đặc trưng của design)
            g2.setColor(COLOR_LIGHT_YELLOW);
            int circleSize = 100;
            // Vẽ đường tròn cắt góc
            g2.fillArc(getWidth() - circleSize / 2, -circleSize / 2, circleSize, circleSize, 180, 90);

            // 3. Vẽ viền xám nhẹ (Shadow giả)
            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}