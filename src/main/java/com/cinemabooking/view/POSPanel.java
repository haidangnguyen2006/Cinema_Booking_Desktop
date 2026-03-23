package com.cinemabooking.view;

import com.cinemabooking.dao.MovieDAO;
import com.cinemabooking.model.Movie;
import com.cinemabooking.model.Seat;
import com.cinemabooking.model.ShowTime;
import com.cinemabooking.service.ShowTimeService;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class POSPanel extends JPanel {

    private final Color COLOR_PRIMARY_YELLOW = new Color(242, 194, 62);
    private final Color COLOR_SEAT_AVAILABLE = new Color(230, 235, 240);
    private final Color COLOR_SEAT_SELECTED = new Color(242, 194, 62);
    private final Color COLOR_SEAT_SOLD = new Color(220, 80, 80);
    private JPanel seatMatrixContainer;
    private JLabel lblBillMovie;
    private JLabel lblBillSeat;
    private JLabel lblBillTotal;

    private List<Seat> selectedSeats = new ArrayList<>();
    private ShowTime currentShowTime;

    private final ShowTimeService showTimeService = new com.cinemabooking.service.ShowTimeService();

    public POSPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Bố cục 3 phần
        add(createLeftPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    // =========================================
    // 1. CỘT TRÁI: CHỌN PHIM VÀ SUẤT CHIẾU
    // =========================================
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(350, 0)); // Tăng độ rộng để chứa ảnh và nút
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitle = new JLabel("Lịch chiếu hôm nay");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        leftPanel.add(lblTitle, BorderLayout.NORTH);

        // Container chứa danh sách các phim (Dùng BoxLayout xếp dọc)
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(Color.WHITE);

        // Bọc vào ScrollPane
        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null); // Xóa viền cho đẹp
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt hơn
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // Dòng trạng thái Loading
        JLabel lblLoading = new JLabel("Đang tải dữ liệu...", SwingConstants.CENTER);
        lblLoading.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        listContainer.add(lblLoading);

        // Load dữ liệu ngầm bằng SwingWorker
        SwingWorker<List<Movie>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Movie> doInBackground() throws Exception {
                MovieDAO movieDAO = new MovieDAO();
                return movieDAO.getAllMovies();
            }

            @Override
            protected void done() {
                try {
                    List<Movie> movies = get();
                    listContainer.removeAll(); // Xóa chữ loading

                    if (movies.isEmpty()) {
                        listContainer.add(new JLabel("Chưa có phim nào."));
                    } else {
                        // Tạo các khung gập mở (Accordion) cho từng phim
                        for (Movie m : movies) {
                            listContainer.add(new MovieAccordionItem(m));
                            listContainer.add(Box.createRigidArea(new Dimension(0, 10))); // Khoảng cách giữa các phim
                        }
                    }

                    // Cập nhật lại giao diện sau khi add component
                    listContainer.revalidate();
                    listContainer.repaint();

                } catch (Exception e) {
                    listContainer.removeAll();
                    listContainer.add(new JLabel("Lỗi tải danh sách phim!"));
                    e.printStackTrace();
                }
            }
        };
        worker.execute();

        return leftPanel;
    }

    // =========================================
    // INNER CLASS: GIAO DIỆN GẬP MỞ CHO TỪNG PHIM
    // =========================================
    class MovieAccordionItem extends JPanel {
        private boolean isExpanded = false;
        private JPanel bodyPanel;
        private JLabel lblIcon;

        public MovieAccordionItem(Movie movie) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true)); // Viền bo góc nhẹ

            // --- 1. HEADER (Tiêu đề phim) ---
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(250, 250, 250));
            headerPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
            headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblTitle = new JLabel(movie.getTitle());
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

            lblIcon = new JLabel("▼"); // Icon chỉ thị trạng thái gập/mở
            lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblIcon.setForeground(Color.GRAY);

            headerPanel.add(lblTitle, BorderLayout.CENTER);
            headerPanel.add(lblIcon, BorderLayout.EAST);

            // --- 2. BODY (Nội dung chi tiết: Poster + Giờ chiếu) ---
            bodyPanel = new JPanel(new BorderLayout(15, 0));
            bodyPanel.setBackground(Color.WHITE);
            bodyPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
            bodyPanel.setVisible(false); // Mặc định ẩn

            // 2.1 Cột trái của Body: Poster Phim
            JLabel lblPoster = new JLabel("Loading...", SwingConstants.CENTER);
            lblPoster.setPreferredSize(new Dimension(80, 120));
            lblPoster.setOpaque(true);
            lblPoster.setBackground(new Color(240, 240, 240));
            bodyPanel.add(lblPoster, BorderLayout.WEST);

            if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
                new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        java.net.URL url = new java.net.URL(movie.getPosterUrl());
                        Image img = javax.imageio.ImageIO.read(url);
                        return new ImageIcon(img.getScaledInstance(80, 120, Image.SCALE_SMOOTH));
                    }
                    @Override
                    protected void done() {
                        try {
                            lblPoster.setText(""); // Xóa chữ Loading
                            lblPoster.setIcon(get());
                        } catch (Exception e) {
                            lblPoster.setText("No Image");
                        }
                    }
                }.execute();
            }

            // 2.2 Cột phải của Body: Danh sách giờ chiếu (Dùng ToggleButton)
            JPanel timesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            timesPanel.setBackground(Color.WHITE);

            // Hiện chữ loading trong lúc chờ DB
            timesPanel.add(new JLabel("Đang tải lịch chiếu..."));
            bodyPanel.add(timesPanel, BorderLayout.CENTER);

            // Gọi DB lấy suất chiếu thật
            new SwingWorker<List<ShowTime>, Void>() {
                @Override
                protected List<ShowTime> doInBackground() throws Exception {
                    return showTimeService.getShowTimesForMovie(movie.getMovieId());
                }

                @Override
                protected void done() {
                    try {
                        List<ShowTime> showTimes = get();
                        timesPanel.removeAll(); // Xóa chữ loading

                        if (showTimes.isEmpty()) {
                            timesPanel.add(new JLabel("Chưa có lịch chiếu."));
                        } else {
                            ButtonGroup timeGroup = new ButtonGroup();
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");

                            for (ShowTime st : showTimes) {
                                // Format thời gian từ DB (vd: 18:00:00 -> 18:00)
                                String timeStr = sdf.format(st.getStartTime());
                                JToggleButton btnTime = new JToggleButton(timeStr);
                                btnTime.setFont(new Font("Segoe UI", Font.BOLD, 13));
                                btnTime.setBackground(new Color(245, 246, 250));
                                btnTime.setFocusPainted(false);
                                btnTime.setCursor(new Cursor(Cursor.HAND_CURSOR));

                                btnTime.addActionListener(e -> {
                                    if (btnTime.isSelected()) {
                                        btnTime.setBackground(COLOR_PRIMARY_YELLOW);
                                        // GỌI HÀM CẬP NHẬT SƠ ĐỒ GHẾ VỚI OBJECT SHOWTIME THẬT
                                        updateSeatMatrixForShowtime(movie, st);
                                    }
                                    if(!btnTime.isSelected()) {
                                        btnTime.setBackground(new Color(245, 246, 250));
                                        // Nếu bỏ chọn giờ chiếu, reset sơ đồ ghế và bill
                                        seatMatrixContainer.removeAll();
                                        seatMatrixContainer.revalidate();
                                        seatMatrixContainer.repaint();
                                        lblBillMovie.setText("Phim: Chưa chọn");
                                        lblBillSeat.setText("Ghế: --");
                                        lblBillTotal.setText("Tổng tiền: 0 VNĐ");
                                    }
                                });

                                timeGroup.add(btnTime);
                                timesPanel.add(btnTime);
                            }
                        }
                        timesPanel.revalidate();
                        timesPanel.repaint();
                    } catch (Exception e) {
                        timesPanel.removeAll();
                        timesPanel.add(new JLabel("Lỗi tải lịch chiếu."));
                    }
                }
            }.execute();
            bodyPanel.add(timesPanel, BorderLayout.CENTER);

            // --- 3. GẮN SỰ KIỆN GẬP/MỞ ---
            headerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    isExpanded = !isExpanded;
                    bodyPanel.setVisible(isExpanded);
                    lblIcon.setText(isExpanded ? "▲" : "▼");

                    Container parent = getParent();
                    if (parent != null) {
                        parent.revalidate();
                        parent.repaint();
                    }
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    headerPanel.setBackground(new Color(240, 240, 240));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    headerPanel.setBackground(new Color(250, 250, 250));
                }
            });

            // Thêm Header và Body vào Item chính
            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);
        }
    }

    // =========================================
    // 2. CỘT GIỮA: SƠ ĐỒ GHẾ (SEAT MATRIX)
    // =========================================
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // --- Hình ảnh Màn hình (Screen) ---
        JLabel lblScreen = new JLabel("M À N   H Ì N H", SwingConstants.CENTER);
        lblScreen.setOpaque(true);
        lblScreen.setBackground(new Color(200, 200, 200));
        lblScreen.setForeground(Color.DARK_GRAY);
        lblScreen.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblScreen.setPreferredSize(new Dimension(0, 40));

        lblScreen.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, COLOR_PRIMARY_YELLOW));
        centerPanel.add(lblScreen, BorderLayout.NORTH);

        seatMatrixContainer = new JPanel();
        seatMatrixContainer.setOpaque(false);


        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(seatMatrixContainer);

        centerPanel.add(wrapper, BorderLayout.CENTER);

        // --- Chú thích màu sắc (Legend) ---
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem("Trống", COLOR_SEAT_AVAILABLE));
        legendPanel.add(createLegendItem("Đang chọn", COLOR_SEAT_SELECTED));
        legendPanel.add(createLegendItem("Đã bán", COLOR_SEAT_SOLD));

        centerPanel.add(legendPanel, BorderLayout.SOUTH);

        return centerPanel;
    }

    private JPanel createLegendItem(String label, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        JLabel box = new JLabel("   "); // Ô vuông màu
        box.setOpaque(true);
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(box);
        panel.add(lbl);
        return panel;
    }

    // =========================================
    // 3. CỘT PHẢI: HÓA ĐƠN THU NGÂN (BILLING)
    // =========================================
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Tiêu đề Bill
        JLabel lblBillTitle = new JLabel("Thông tin Hóa đơn");
        lblBillTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightPanel.add(lblBillTitle, BorderLayout.NORTH);

        // Chi tiết
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        lblBillMovie = new JLabel("Phim: Chưa chọn");
        lblBillSeat = new JLabel("Ghế: --");

        detailsPanel.add(lblBillMovie);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(lblBillSeat);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Nhập khách hàng (Tích điểm)
        JPanel customerPanel = new JPanel(new BorderLayout(5, 5));
        customerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        customerPanel.setOpaque(false);
        customerPanel.setBorder(new TitledBorder("Khách hàng thành viên"));
        JTextField txtPhone = new JTextField();
        txtPhone.putClientProperty("JTextField.placeholderText", "Nhập SĐT khách...");
        JButton btnCheck = new JButton("Kiểm tra");
        customerPanel.add(txtPhone, BorderLayout.CENTER);
        customerPanel.add(btnCheck, BorderLayout.EAST);
        detailsPanel.add(customerPanel);
        detailsPanel.add(Box.createVerticalGlue());
        rightPanel.add(detailsPanel, BorderLayout.CENTER);

        // Tổng tiền & Thanh toán
        JPanel checkoutPanel = new JPanel(new BorderLayout(0, 10));
        checkoutPanel.setOpaque(false);

        lblBillTotal = new JLabel("Tổng tiền: 0 VNĐ");
        lblBillTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBillTotal.setForeground(new Color(220, 50, 50));

        JButton btnCheckout = new JButton("THANH TOÁN (F5)");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCheckout.setBackground(COLOR_PRIMARY_YELLOW);
        btnCheckout.setPreferredSize(new Dimension(0, 50));
        btnCheckout.setFocusPainted(false);

        checkoutPanel.add(lblBillTotal, BorderLayout.NORTH);
        checkoutPanel.add(btnCheckout, BorderLayout.CENTER);

        rightPanel.add(checkoutPanel, BorderLayout.SOUTH);

        return rightPanel;
    }
    public void updateSeatMatrixForShowtime(Movie movie, ShowTime showTime) {
        this.currentShowTime = showTime;

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        String timeStr = sdf.format(showTime.getStartTime());

        // 1. Cập nhật Bill
        lblBillMovie.setText("Phim: " + movie.getTitle() + " (" + timeStr + ")");
        selectedSeats.clear();
        updateBillDetails();

        // 2. Hiện trạng thái loading cho ghế
        seatMatrixContainer.removeAll();
        seatMatrixContainer.setLayout(new GridBagLayout());
        seatMatrixContainer.add(new JLabel("Đang tải sơ đồ ghế từ hệ thống..."));
        seatMatrixContainer.revalidate();
        seatMatrixContainer.repaint();

        // 3. Gọi DB lấy danh sách ghế thật
        new SwingWorker<List<Seat>, Void>() {
            @Override
            protected List<Seat> doInBackground() throws Exception {
                // Lấy ghế dựa vào RoomID và ShowTimeID để check vé
                return showTimeService.getSeatsForShowTime(showTime.getRoomId(), showTime.getShowTimeId());
            }

            @Override
            protected void done() {
                try {
                    List<Seat> seats = get();
                    seatMatrixContainer.removeAll();

                    if (seats.isEmpty()) {
                        seatMatrixContainer.add(new JLabel("Phòng chiếu này chưa được thiết lập ghế."));
                    } else {
                        seatMatrixContainer.setLayout(new GridLayout(0, 10, 8, 8));

                        for (Seat seat : seats) {
                            JToggleButton btnSeat = new JToggleButton(seat.getSeatName());
                            btnSeat.setFont(new Font("Segoe UI", Font.BOLD, 12));
                            btnSeat.setFocusPainted(false);
                            btnSeat.setCursor(new Cursor(Cursor.HAND_CURSOR));

                            if (seat.isSold()) {
                                // GHẾ ĐÃ BÁN
                                btnSeat.setBackground(COLOR_SEAT_SOLD);
                                btnSeat.setEnabled(false);
                            } else {
                                // GHẾ TRỐNG
                                btnSeat.setBackground(COLOR_SEAT_AVAILABLE);
                                btnSeat.addActionListener(e -> {
                                    if (btnSeat.isSelected()) {
                                        btnSeat.setBackground(COLOR_SEAT_SELECTED);
                                        selectedSeats.add(seat); // Thêm Object Seat vào giỏ
                                    } else {
                                        btnSeat.setBackground(COLOR_SEAT_AVAILABLE);
                                        // Xóa Object Seat khỏi giỏ dựa vào ID
                                        selectedSeats.removeIf(s -> s.getSeatId() == seat.getSeatId());
                                    }
                                    updateBillDetails();
                                });
                            }
                            seatMatrixContainer.add(btnSeat);
                        }
                    }
                    seatMatrixContainer.revalidate();
                    seatMatrixContainer.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // =========================================
    // Tính tiền
    // =========================================
    private void updateBillDetails() {
        if (selectedSeats.isEmpty() || currentShowTime == null) {
            lblBillSeat.setText("Ghế: --");
            lblBillTotal.setText("Tổng tiền: 0 VNĐ");
        } else {
            List<String> seatNames = new ArrayList<>();
            for (Seat s : selectedSeats) {
                seatNames.add(s.getSeatName());
            }
            lblBillSeat.setText("Ghế: " + String.join(", ", seatNames));

            double total = selectedSeats.size() * currentShowTime.getTicketPrice();
            lblBillTotal.setText(String.format("Tổng tiền: %,.0f VNĐ", total));
        }
    }
}