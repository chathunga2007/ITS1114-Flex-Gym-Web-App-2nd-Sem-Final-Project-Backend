package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.OrderItemDTO;
import java.math.BigDecimal;
import java.util.List;

public interface EmailService {
    void sendAccountCredentialsEmail(String toEmail, String name, String password);
    void sendOrderReceiptEmail(String toEmail, String memberName, Long orderId, BigDecimal totalAmount, List<OrderItemDTO> items);
    void sendMembershipExpiryReminderEmail(String toEmail, String memberName, String packageName, String expiryDate, int daysRemaining);
    void sendMembershipExpiredEmail(String toEmail, String memberName, String packageName, String expiredDate);
}