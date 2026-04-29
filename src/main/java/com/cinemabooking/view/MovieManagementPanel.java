package com.cinemabooking.view;

import com.cinemabooking.dao.MovieDAO;
import com.cinemabooking.model.Movie;
import com.cinemabooking.service.TMDBApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MovieManagementPanel extends JPanel {

    private JTable movieTable;
    private DefaultTableModel tableModel;
    private final MovieDAO movieDAO = new MovieDAO();
    private final TMDBApiService tmdbApiService = new TMDBApiService();

    public MovieManagementPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        add(createHeaderPanel(), BorderLayout.NORTH);

        add(createTablePanel(), BorderLayout.CENTER);

        loadMovieData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Tiêu đề
        JLabel lblTitle = new JLabel("Quản lý danh sách Phim");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Khu vực nút bấm
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton btnDelete = new JButton("Xóa phim");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setBackground(new Color(220, 80, 80));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deleteSelectedMovie());

        JButton btnRefresh = new JButton("Làm mới (F5)");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadMovieData());

        JButton btnSync = new JButton("ĐỒNG BỘ TMDB");
        btnSync.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSync.setBackground(new Color(242, 194, 62)); // Vàng đặc trưng
        btnSync.setFocusPainted(false);
        btnSync.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSync.addActionListener(e -> syncMoviesFromTMDB());

        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        actionPanel.add(btnSync);

        headerPanel.add(actionPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Khởi tạo Model cho Bảng
        String[] columns = {"ID", "TMDB ID", "Tên phim", "Ngày phát hành", "Thời lượng (Phút)", "Thể loại", "Rating"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        movieTable = new JTable(tableModel);
        movieTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        movieTable.setRowHeight(35);
        movieTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        movieTable.getTableHeader().setBackground(new Color(240, 240, 240));
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        movieTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        movieTable.getColumnModel().getColumn(2).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(movieTable);
        scrollPane.setBorder(null);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    
    // HÀM TẢI DỮ LIỆU TỪ DATABASE LÊN BẢNG
    
    private void loadMovieData() {
        new SwingWorker<List<Movie>, Void>() {
            @Override
            protected List<Movie> doInBackground() throws Exception {
                return movieDAO.getAllMovies();
            }

            @Override
            protected void done() {
                try {
                    List<Movie> movies = get();
                    tableModel.setRowCount(0);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    for (Movie m : movies) {
                        String releaseDate = (m.getReleaseDate() != null) ? sdf.format(m.getReleaseDate()) : "N/A";
                        tableModel.addRow(new Object[]{
                                m.getMovieId(),
                                m.getTmdbId(),
                                m.getTitle(),
                                releaseDate,
                                m.getDuration(),
                                m.getGenre(),
                                m.getRating()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MovieManagementPanel.this, "Lỗi tải dữ liệu phim: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    
    // HÀM ĐỒNG BỘ API TMDB VỚI THANH LOADING
    
    private void syncMoviesFromTMDB() {
        JDialog loadingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đang xử lý", true);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Đang kéo dữ liệu phim mới nhất từ mạng, vui lòng đợi..."), BorderLayout.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); 
        panel.add(progressBar, BorderLayout.SOUTH);
        loadingDialog.add(panel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(this);

        // Chạy Background Task
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                tmdbApiService.fetchAndSaveNowPlayingMovies();
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    get();
                    JOptionPane.showMessageDialog(MovieManagementPanel.this, "Đồng bộ phim thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadMovieData(); 
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MovieManagementPanel.this, "Lỗi khi đồng bộ TMDB: " + e.getMessage(), "Lỗi mạng", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }
    private void deleteSelectedMovie() {
        // 1. Kiểm tra xem người dùng đã chọn dòng nào trên bảng chưa
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng click chọn một bộ phim trên bảng để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy ID và Tên phim từ dòng đang chọn
        int movieId = (int) tableModel.getValueAt(selectedRow, 0);
        String movieTitle = (String) tableModel.getValueAt(selectedRow, 2);

        // 3. Hộp thoại xác nhận
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa bộ phim: '" + movieTitle + "' khỏi hệ thống?\nHành động này không thể hoàn tác!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return movieDAO.deleteMovie(movieId);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(MovieManagementPanel.this, "Đã xóa phim thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            loadMovieData();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MovieManagementPanel.this, ex.getMessage(), "Không thể xóa", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}