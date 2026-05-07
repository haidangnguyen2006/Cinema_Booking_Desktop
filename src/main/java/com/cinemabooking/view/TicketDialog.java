package com.cinemabooking.view;

import com.cinemabooking.model.Customer;
import com.cinemabooking.model.Seat;
import com.cinemabooking.model.ShowTime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class TicketDialog extends JDialog {

    public TicketDialog(Frame parent, String movieName, ShowTime showTime, List<Seat> seats, Customer customer, double total, double discount, double finalAmt) {
        super(parent, "In Vé Xem Phim", true); // Modal dialog
        setSize(400, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel ticketPanel = new JPanel();
        ticketPanel.setLayout(new BoxLayout(ticketPanel, BoxLayout.Y_AXIS));
        ticketPanel.setBackground(Color.WHITE);
        ticketPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 20, 20, 20),
                BorderFactory.createDashedBorder(Color.GRAY, 3, 2, 5, true)
        ));

        JLabel lblHeader = new JLabel("IUH CINEMA");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubHeader = new JLabel("VÉ XEM PHIM / TICKET RECEIPT");
        lblSubHeader.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        ticketPanel.add(lblHeader);
        ticketPanel.add(lblSubHeader);
        ticketPanel.add(Box.createVerticalStrut(20));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        String dateStr = sdfDate.format(showTime.getStartTime());
        String timeStr = sdfTime.format(showTime.getStartTime());

        String seatStr = seats.stream().map(Seat::getSeatName).collect(Collectors.joining(", "));

        detailsPanel.add(createReceiptLine("Phim (Movie):", movieName));
        detailsPanel.add(createReceiptLine("Ngày (Date):", dateStr));
        detailsPanel.add(createReceiptLine("Giờ (Time):", timeStr));
        detailsPanel.add(createReceiptLine("Phòng (Cinema):", "Room " + showTime.getRoom().getRoomId()));
        detailsPanel.add(createReceiptLine("Ghế (Seats):", seatStr));

        if (customer != null) {
            detailsPanel.add(createReceiptLine("Khách hàng:", customer.getFullName()));
        }

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        detailsPanel.add(sep);
        detailsPanel.add(Box.createVerticalStrut(8));

        detailsPanel.add(createReceiptLine("Tổng tiền:", String.format("%,.0f VNĐ", total)));
        if (discount > 0) {
            detailsPanel.add(createReceiptLine("Giảm giá:", String.format("-%,.0f VNĐ", discount)));
        }

        JPanel finalPanel = new JPanel(new BorderLayout());
        finalPanel.setBackground(Color.WHITE);
        finalPanel.setMaximumSize(new Dimension(400, 30));
        JLabel lblFinal = new JLabel(String.format("THỰC THU: %,.0f VNĐ", finalAmt));
        lblFinal.setFont(new Font("Consolas", Font.BOLD, 16));
        lblFinal.setHorizontalAlignment(SwingConstants.RIGHT);
        finalPanel.add(lblFinal, BorderLayout.EAST);
        detailsPanel.add(finalPanel);

        ticketPanel.add(detailsPanel);
        ticketPanel.add(Box.createVerticalStrut(30));

        JLabel lblBarcode = new JLabel("|||| | || ||| || ||| | ||");
        lblBarcode.setFont(new Font("Consolas", Font.PLAIN, 36));
        lblBarcode.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblThanks = new JLabel("Chúc bạn xem phim vui vẻ!");
        lblThanks.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblThanks.setAlignmentX(Component.CENTER_ALIGNMENT);

        ticketPanel.add(lblBarcode);
        ticketPanel.add(lblThanks);

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        wrapperPanel.setOpaque(false);
        ticketPanel.setPreferredSize(new Dimension(320, 480));
        wrapperPanel.add(ticketPanel);

        add(wrapperPanel, BorderLayout.CENTER);

        // --- NÚT ĐÓNG / HOÀN TẤT ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        JButton btnClose = new JButton("HOÀN TẤT & IN VÉ");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBackground(new Color(242, 194, 62));
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(200, 40));
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createReceiptLine(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));

        panel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        JLabel lblLeft = new JLabel(label);
        lblLeft.setFont(new Font("Consolas", Font.PLAIN, 14));
        lblLeft.setForeground(Color.DARK_GRAY);
        lblLeft.setVerticalAlignment(SwingConstants.TOP);
        lblLeft.setPreferredSize(new Dimension(200, 20));

        String htmlValue = String.format("<html><div style='width: 140px; text-align: right; word-wrap: break-word;'>%s</div></html>", value);

        JLabel lblRight = new JLabel(htmlValue);
        lblRight.setFont(new Font("Consolas", Font.BOLD, 14));
        lblRight.setVerticalAlignment(SwingConstants.TOP);

        panel.add(lblLeft, BorderLayout.WEST);
        panel.add(lblRight, BorderLayout.EAST);
        return panel;
    }
}