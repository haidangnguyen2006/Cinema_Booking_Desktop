package com.cinemabooking.service;

import com.cinemabooking.dao.InvoiceDAO;
import com.cinemabooking.model.Customer;
import com.cinemabooking.model.Invoice;
import com.cinemabooking.model.Ticket;
import com.cinemabooking.utils.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class BookingService {
    private final InvoiceDAO invoiceDAO;

    public BookingService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public boolean processPayment(List<Ticket> tickets, Customer customer, double totalAmount) throws SQLException {
        Invoice invoice = new Invoice();

        // Lấy ID của nhân viên đang đăng nhập
        invoice.setStaffId(SessionManager.getCurrentUser().getUserId());
        invoice.setTotalAmount(totalAmount);

        double discountAmount = 0;
        double finalAmount = totalAmount;
        boolean isDiscountApplied = false;

        // Xử lý Khách hàng thành viên & Điểm
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

        return invoiceDAO.createInvoiceTransaction(invoice, tickets, isDiscountApplied);
    }
}
