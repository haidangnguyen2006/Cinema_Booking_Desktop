package com.cinemabooking.view;

import com.cinemabooking.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();

        UIManager.put("TextComponent.arc", 15);
        UIManager.put("Button.arc", 15);
        UIManager.put("CheckBox.icon.style", "filled");

        setTitle("Cinema Booking - Login");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        /*setResizable(false);*/
        initComponents();
    }

    private void initComponents() {
        // 1. Panel Nền (Chứa ảnh background)
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/bg_login.jpeg"));
                    g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(20, 20, 20));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // 2. Panel Đăng nhập
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(45, 45, 45, 220)); // Màu xám tối, độ trong suốt 220/255
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Bo góc 30px
                g2.dispose();
                super.paintComponent(g);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(380, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // TITLE / LOGO
        JPanel pnlLogo=new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pnlLogo.setOpaque(false);
        ImageIcon iuhLogo=new ImageIcon(getClass().getResource("/icons/iuh-logo.png"));
        Image imgIuhLogo=iuhLogo.getImage();
        Image newimgIuh=imgIuhLogo.getScaledInstance(64,64,Image.SCALE_SMOOTH);
        ImageIcon scaledImgIuh=new ImageIcon(newimgIuh);
        JLabel lblIuhLogo=new JLabel(scaledImgIuh, SwingConstants.CENTER);
        JLabel lblTitle = new JLabel("IUH CINEMA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 30, 30, 30);
        pnlLogo.add(lblIuhLogo);
        pnlLogo.add(lblTitle);
        formPanel.add(pnlLogo,gbc);

        // USERNAME INPUT
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(300, 45));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.putClientProperty("JTextField.placeholderText", "Account");
        txtUsername.putClientProperty("JTextField.showClearButton", true);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 30, 10, 30);
        formPanel.add(txtUsername, gbc);

        // PASSWORD INPUT
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 45));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.putClientProperty("JTextField.placeholderText", "Password");
        txtPassword.putClientProperty("JTextField.showRevealButton", true); // Hiện nút xem mật khẩu con mắt
        txtPassword.addActionListener(this::handleLogin);
        gbc.gridy = 2;
        formPanel.add(txtPassword, gbc);

        // CHECKBOX QUÊN MẬT KHẨU
        JCheckBox chkRemember = new JCheckBox("Quên mật khẩu");
        chkRemember.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkRemember.setForeground(new Color(200, 200, 200));
        chkRemember.setOpaque(false); // Xóa nền checkbox
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 30, 15, 30);
        formPanel.add(chkRemember, gbc);

        // LOGIN BUTTON
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(242, 194, 62)); // Màu vàng nhạt
        btnLogin.setForeground(new Color(40, 40, 40));   // Chữ đen xám
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this::handleLogin);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 30, 40, 30);
        formPanel.add(btnLogin, gbc);

        backgroundPanel.add(formPanel);


        setContentPane(backgroundPanel);
    }

    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    boolean isSuccess = get();
                    if (isSuccess) {
                        new MainDashboardFrame().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Lỗi kết nối CSDL!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                }
            }
        };
        worker.execute();
    }
}
