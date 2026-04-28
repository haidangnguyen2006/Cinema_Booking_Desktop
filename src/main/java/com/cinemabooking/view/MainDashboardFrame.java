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
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public MainDashboardFrame() {
        setTitle("Cinema POS System - Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {

        // 1. Thêm Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. KHỞI TẠO CARDLAYOUT CHO KHU VỰC CENTER
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createMainContentPanel(), "VIEW_HOME");
        cardPanel.add(new POSPanel(), "VIEW_POS");
        cardPanel.add(new MovieManagementPanel(), "VIEW_MOVIE");
        cardPanel.add(new ShowTimeManagementPanel(), "VIEW_SHOWTIME");
        add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, "VIEW_HOME");
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

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rightNav.setOpaque(false);

        String staffName = SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getFullName() : "Admin";
        JLabel lblUser = new JLabel("Xin chào, " + staffName);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUser.setForeground(Color.WHITE);

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
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(COLOR_BG_LIGHT);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 30));
        mainPanel.setOpaque(false);
        mainPanel.setPreferredSize(new Dimension(1000, 600));

        JPanel greetingPanel = new JPanel();
        greetingPanel.setLayout(new BoxLayout(greetingPanel, BoxLayout.Y_AXIS));
        greetingPanel.setOpaque(false);

        String staffName = SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getFullName() : "Tên nhân viên";
        JLabel lblWelcome = new JLabel("Xin chào, " + staffName);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 36));

        JLabel lblSub = new JLabel("Chào mừng bạn trở lại hệ thống quản lý rạp chiếu phim!");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(Color.GRAY);

        greetingPanel.add(lblWelcome);
        greetingPanel.add(Box.createVerticalStrut(5));
        greetingPanel.add(lblSub);
        greetingPanel.add(Box.createVerticalStrut(40));

        mainPanel.add(greetingPanel, BorderLayout.NORTH);

        // --- Lưới các thẻ chức năng ---
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 40, 40));
        gridPanel.setOpaque(false);

        DashboardCard cardBanVe = new DashboardCard("Bán vé",
                "Quản lý và bán vé cho khách hàng", new ImageIcon(getClass().getResource("/icons/cinema-ticket-100.png")));
        DashboardCard cardPhim = new DashboardCard("Phim",
                "Quản lý và thông tin phim", new ImageIcon(getClass().getResource("/icons/film-96.png")));
        DashboardCard cardLichChieu = new DashboardCard("Lịch chiếu",
                "Sắp xếp và quản lý lịch chiếu", new ImageIcon(getClass().getResource("/icons/calendar-100.png")));
        DashboardCard cardThongKe = new DashboardCard("Thống kê",
                "Thống kê và xuất báo cáo", new ImageIcon(getClass().getResource("/icons/statistic-96.png")));

        // Click event on card
        cardBanVe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "VIEW_POS");
            }
        });

        cardPhim.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "VIEW_MOVIE");
            }
        });

        cardLichChieu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "VIEW_SHOWTIME");
            }
        });
        gridPanel.add(cardBanVe);
        gridPanel.add(cardPhim);
        gridPanel.add(cardLichChieu);
        gridPanel.add(cardThongKe);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerWrapper.setOpaque(false);
        gridPanel.setPreferredSize(new Dimension(1000, 400));
        centerWrapper.add(gridPanel);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        wrapperPanel.add(mainPanel);

        return wrapperPanel;
    }

    class DashboardCard extends JPanel {

        public DashboardCard(String title, String description, ImageIcon iconSymbol) {
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setLayout(new BorderLayout(25, 0));
            setBorder(new EmptyBorder(25, 30, 25, 30)); // Padding bên trong thẻ

            // Hiệu ứng hover
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_PRIMARY_YELLOW, 2),
                        new EmptyBorder(23, 28, 23, 28)
                )); }
                public void mouseExited(MouseEvent e) { setBorder(new EmptyBorder(25, 30, 25, 30)); }
            });

            // --- 1. Khu vực Icon (Bên trái) ---
            JLabel lblIcon = new JLabel();
            try {
                Image img = iconSymbol.getImage();
                Image newImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);

                ImageIcon scaledIcon = new ImageIcon(newImg);

                lblIcon = new JLabel(scaledIcon, SwingConstants.CENTER);
                lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));
            } catch (Exception e) {
                lblIcon.setText("ICON");
                lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
            }
            add(lblIcon, BorderLayout.WEST);

            // --- 2. Khu vực Chữ (Bên phải) ---
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblDesc = new JLabel(description);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblDesc.setForeground(new Color(120, 120, 120));
            lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

            textPanel.add(Box.createVerticalGlue());
            textPanel.add(lblTitle);
            textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            textPanel.add(lblDesc);
            textPanel.add(Box.createVerticalGlue());

            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            g2.setColor(COLOR_LIGHT_YELLOW);
            int circleSize = 110;
            g2.fillArc(getWidth() - circleSize / 2, -circleSize / 2, circleSize, circleSize, 180, 90);

            g2.setColor(new Color(220, 220, 225));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}