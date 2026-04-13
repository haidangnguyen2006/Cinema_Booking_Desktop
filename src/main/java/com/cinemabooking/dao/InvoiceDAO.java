package com.cinemabooking.dao;

import com.cinemabooking.model.Invoice;
import com.cinemabooking.model.Ticket;
import com.cinemabooking.utils.DatabaseConnection;

import java.sql.*;
import java.util.List;

public class InvoiceDAO {
    public boolean createInvoiceTransaction(Invoice invoice, List<Ticket> tickets, boolean isDiscountApplied) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // 1. Tắt AutoCommit
        conn.setAutoCommit(false);

        String sqlInsertInvoice = "INSERT INTO Invoices (StaffID, CustomerPhone, TotalAmount, DiscountAmount, FinalAmount, EarnedPoints) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlInsertTicket = "INSERT INTO Tickets (InvoiceID, ShowTimeID, SeatID, Price, Status) VALUES (?, ?, ?, ?, 'Confirmed')";
        String sqlUpdateCustomer = "UPDATE Customers SET Points = Points - ? + ? WHERE Phone = ?";

        try (PreparedStatement stmtInvoice = conn.prepareStatement(sqlInsertInvoice, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtTicket = conn.prepareStatement(sqlInsertTicket);
             PreparedStatement stmtCustomer = conn.prepareStatement(sqlUpdateCustomer)) {

            // BƯỚC 1: LƯU HÓA ĐƠN
            stmtInvoice.setString(1, invoice.getStaffId());
            if (invoice.getCustomerPhone() != null && !invoice.getCustomerPhone().isEmpty()) {
                stmtInvoice.setString(2, invoice.getCustomerPhone());
            } else {
                stmtInvoice.setNull(2, Types.NVARCHAR);
            }
            stmtInvoice.setDouble(3, invoice.getTotalAmount());
            stmtInvoice.setDouble(4, invoice.getDiscountAmount());
            stmtInvoice.setDouble(5, invoice.getFinalAmount());
            stmtInvoice.setInt(6, invoice.getEarnedPoints());

            stmtInvoice.executeUpdate();

            // Lấy InvoiceID vừa được SQL Server tự sinh ra
            ResultSet rs = stmtInvoice.getGeneratedKeys();
            int generatedInvoiceId = 0;
            if (rs.next()) {
                generatedInvoiceId = rs.getInt(1);
            } else {
                throw new SQLException("Không thể lấy ID hóa đơn vừa tạo!");
            }

            // BƯỚC 2: LƯU DANH SÁCH VÉ
            for (Ticket t : tickets) {
                stmtTicket.setInt(1, generatedInvoiceId);
                stmtTicket.setInt(2, t.getShowTimeId());
                stmtTicket.setInt(3, t.getSeatId());
                stmtTicket.setDouble(4, t.getPrice());
                stmtTicket.addBatch();
            }
            stmtTicket.executeBatch();

            // BƯỚC 3: CẬP NHẬT ĐIỂM KHÁCH HÀNG
            if (invoice.getCustomerPhone() != null && !invoice.getCustomerPhone().isEmpty()) {
                int pointsToDeduct = isDiscountApplied ? 200 : 0;
                stmtCustomer.setInt(1, pointsToDeduct);
                stmtCustomer.setInt(2, invoice.getEarnedPoints());
                stmtCustomer.setString(3, invoice.getCustomerPhone());
                stmtCustomer.executeUpdate();
            }

            // BƯỚC 4: XÁC NHẬN THÀNH CÔNG
            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
