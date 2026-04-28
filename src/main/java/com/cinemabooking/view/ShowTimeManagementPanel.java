package com.cinemabooking.view;

import com.cinemabooking.dao.MovieDAO;
import com.cinemabooking.dao.RoomDAO;
import com.cinemabooking.dao.ShowTimeDAO;
import com.cinemabooking.model.Movie;
import com.cinemabooking.model.ShowTime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowTimeManagementPanel extends JPanel {

    private final ShowTimeDAO showTimeDAO = new ShowTimeDAO();
    private final MovieDAO movieDAO = new MovieDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    // Components cho Form
    private JComboBox<MovieItem> cbMovies;
    private JComboBox<RoomItem> cbRooms;
    private JTextField txtDate; // Format: dd/MM/yyyy
    private JComboBox<String> cbHour;
    private JComboBox<String> cbMinute;
    private JTextField txtPrice;

    public ShowTimeManagementPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Quản lý Lịch Chiếu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(lblTitle, BorderLayout.NORTH);

        // --- BÊN TRÁI: FORM THÊM MỚI ---
        add(createFormPanel(), BorderLayout.WEST);

        // --- BÊN PHẢI: BẢNG DANH SÁCH ---
        add(createTablePanel(), BorderLayout.CENTER);

        // Tải dữ liệu ban đầu
        loadFormOptions();
        loadTableData();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(350, 0));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createTitledBorder(null, "Thêm Suất Chiếu Mới", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Segoe UI", Font.BOLD, 14))
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // 1. Chọn Phim
        gbc.gridy = 0; formPanel.add(new JLabel("Chọn Phim:"), gbc);
        cbMovies = new JComboBox<>();
        gbc.gridy = 1; formPanel.add(cbMovies, gbc);

        // 2. Chọn Phòng
        gbc.gridy = 2; formPanel.add(new JLabel("Phòng chiếu:"), gbc);
        cbRooms = new JComboBox<>();
        gbc.gridy = 3; formPanel.add(cbRooms, gbc);

        // 3. Ngày chiếu
        gbc.gridy = 4; formPanel.add(new JLabel("Ngày chiếu (dd/MM/yyyy):"), gbc);
        txtDate = new JTextField();
        // Mặc định điền ngày hôm nay
        txtDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        gbc.gridy = 5; formPanel.add(txtDate, gbc);

        // 4. Giờ chiếu (Dùng 2 combobox cho Giờ và Phút)
        gbc.gridy = 6; formPanel.add(new JLabel("Giờ chiếu:"), gbc);
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setOpaque(false);

        String[] hours = {"08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        cbHour = new JComboBox<>(hours);
        String[] minutes = {"00", "15", "30", "45"};
        cbMinute = new JComboBox<>(minutes);

        timePanel.add(cbHour);
        timePanel.add(new JLabel(":"));
        timePanel.add(cbMinute);
        gbc.gridy = 7; formPanel.add(timePanel, gbc);

        // 5. Giá vé
        gbc.gridy = 8; formPanel.add(new JLabel("Giá vé (VNĐ):"), gbc);
        txtPrice = new JTextField("75000"); // Giá mặc định
        gbc.gridy = 9; formPanel.add(txtPrice, gbc);

        // 6. Nút Thêm
        JButton btnAdd = new JButton("LƯU SUẤT CHIẾU");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(new Color(242, 194, 62));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> saveShowTime());

        gbc.gridy = 10;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(btnAdd, gbc);

        // Đẩy các component lên trên cùng
        gbc.gridy = 11; gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Các nút thao tác với bảng
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton btnDelete = new JButton("Xóa Suất Chọn");
        btnDelete.setBackground(new Color(220, 80, 80));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedShowTime());
        actionPanel.add(btnDelete);
        panel.add(actionPanel, BorderLayout.NORTH);

        // Khởi tạo bảng
        String[] columns = {"ID", "Tên Phim", "Phòng", "Thời gian chiếu", "Giá vé"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ LÔ-GIC (TẢI DỮ LIỆU & LƯU)
    // ==========================================
    private void loadFormOptions() {
        try {
            List<Movie> movies = movieDAO.getAllMovies();
            for (Movie m : movies) {
                cbMovies.addItem(new MovieItem(m.getMovieId(), m.getTitle()));
            }

            List<com.cinemabooking.model.Room> rooms = roomDAO.getAllRooms();
            for (com.cinemabooking.model.Room r : rooms) {
                cbRooms.addItem(new RoomItem(r.getRoomId(), r.getRoomName()));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Phim/Phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                return showTimeDAO.getAllShowTimesForTable();
            }

            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                    for (Object[] row : get()) {
                        Timestamp ts = (Timestamp) row[3];
                        row[3] = sdf.format(ts); // Format lại giờ cho đẹp

                        double price = (double) row[4];
                        row[4] = String.format("%,.0f đ", price);
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void saveShowTime() {
        try {
            // Lấy dữ liệu từ Form
            MovieItem selectedMovie = (MovieItem) cbMovies.getSelectedItem();
            RoomItem selectedRoom = (RoomItem) cbRooms.getSelectedItem();
            double price = Double.parseDouble(txtPrice.getText().trim());

            // Parse thời gian nhập vào thành Timestamp của SQL
            String dateStr = txtDate.getText().trim();
            String timeStr = cbHour.getSelectedItem() + ":" + cbMinute.getSelectedItem() + ":00";
            String fullDateTimeStr = dateStr + " " + timeStr;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            java.util.Date parsedDate = sdf.parse(fullDateTimeStr);
            Timestamp startTime = new Timestamp(parsedDate.getTime());

            // Kiểm tra trùng lịch phòng chiếu
            if (!showTimeDAO.isRoomAvailable(selectedRoom.id, startTime)) {
                JOptionPane.showMessageDialog(this,
                        "CẢNH BÁO TRÙNG LỊCH: \nPhòng '" + selectedRoom.name + "' đã có một bộ phim khác chiếu vào khoảng thời gian này (Cần cách nhau ít nhất 120 phút).",
                        "Lỗi xếp lịch", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lưu xuống DB
            ShowTime st = new ShowTime();
            st.setMovieId(selectedMovie.id);
            st.setRoomId(selectedRoom.id);
            st.setStartTime(startTime);
            st.setTicketPrice(price);

            if (showTimeDAO.insertShowTime(st)) {
                JOptionPane.showMessageDialog(this, "Thêm Lịch chiếu thành công!");
                loadTableData();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedShowTime() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa lịch chiếu này?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                showTimeDAO.deleteShowTime(id);
                JOptionPane.showMessageDialog(this, "Đã xóa!");
                loadTableData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class MovieItem {
        int id; String title;
        public MovieItem(int id, String title) { this.id = id; this.title = title; }
        @Override public String toString() { return title; }
    }

    class RoomItem {
        int id; String name;
        public RoomItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }
}