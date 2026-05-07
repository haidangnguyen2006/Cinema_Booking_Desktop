package com.cinemabooking.service;

import com.cinemabooking.dao.InvoiceDAO;
import com.cinemabooking.model.*;
import com.cinemabooking.utils.SessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private final InvoiceDAO invoiceDAO;

    public BookingService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public boolean processPayment(ShowTime showTime, List<Seat> seats, Customer customer) throws SQLException {
        // 1. Tính tổng tiền gốc
        double totalAmount = seats.size() * showTime.getTicketPrice();

        // 2. Chuyển đổi List<Seat> sang List<Ticket> cho DAO
        List<Ticket> tickets = new ArrayList<>();
        for (Seat seat : seats) {
            Ticket ticket = new Ticket();
            ticket.setShowTime(showTime);
            ticket.setSeat(seat);
            ticket.setPrice(showTime.getTicketPrice());
            ticket.setStatus("Confirmed");
            tickets.add(ticket);
        }

        // 3. Khởi tạo Hóa đơn (Invoice)
        Invoice invoice = new Invoice();

        // Lấy ID của nhân viên đang đăng nhập (An toàn)
        if (SessionManager.isLoggedIn()) {
            invoice.setStaffId(SessionManager.getCurrentUser().getUserId());
        } else {
            invoice.setStaffId("NV01");
        }

        invoice.setTotalAmount(totalAmount);

        double discountAmount = 0;
        double finalAmount = totalAmount;
        boolean isDiscountApplied = false;

        if (customer != null) {
            invoice.setCustomerPhone(customer.getPhone());

            // QUY TẮC BR02 & LUỒNG SỰ KIỆN: Điểm >= 200 thì giảm 20k
            if (customer.getPoints() >= 200) {
                discountAmount = 20000;
                isDiscountApplied = true;
            }

            finalAmount = totalAmount - discountAmount;
            if (finalAmount < 0) finalAmount = 0;

            // QUY TẮC BR01 & BR03: Tính điểm dựa trên tiền thực trả, bỏ phần dư dưới 50k
            int earnedPoints = (int) (finalAmount / 50000) * 10;
            invoice.setEarnedPoints(earnedPoints);

        } else {
            // Khách vãng lai: Không điểm, Không giảm giá
            invoice.setEarnedPoints(0);
        }

        invoice.setDiscountAmount(discountAmount);
        invoice.setFinalAmount(finalAmount);

        // 5. Đẩy cục dữ liệu xuống InvoiceDAO
        return invoiceDAO.createInvoiceTransaction(invoice, tickets, isDiscountApplied);
    }
}
