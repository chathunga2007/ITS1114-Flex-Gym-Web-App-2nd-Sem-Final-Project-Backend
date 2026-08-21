package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.OrderItemDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendAccountCredentialsEmail(String toEmail, String name, String password) {
        log.info("Sending credentials email to: {}", toEmail);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Welcome to Flex Gym - Your Account Credentials");
            message.setText("Hello " + name + ",\n\n" +
                    "Welcome to Flex Gym! Your account has been created successfully.\n\n" +
                    "Email / Username: " + toEmail + "\n" +
                    "Password: " + password + "\n\n" +
                    "Please change your password after logging in for security.\n\n" +
                    "Best Regards,\n" +
                    "Flex Gym Team");

            mailSender.send(message);
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendOrderReceiptEmail(String toEmail, String memberName, Long orderId, BigDecimal totalAmount, List<OrderItemDTO> items) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Flex Gym - Order Receipt #" + orderId);

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Hello ").append(memberName).append(",\n\n");
            emailBody.append("Thank you for your purchase! Your order has been successfully placed.\n\n");
            emailBody.append("Order ID: ").append(orderId).append("\n");
            emailBody.append("Total Amount: Rs. ").append(totalAmount).append("\n\n");
            emailBody.append("Purchased Items:\n");

            for (OrderItemDTO item : items) {
                emailBody.append("- ").append(item.getProductName())
                        .append(" (Qty: ").append(item.getQuantity())
                        .append(", Unit Price: Rs. ").append(item.getUnitPrice()).append(")\n");
            }

            emailBody.append("\nStay fit with Flex Gym!\n");
            message.setText(emailBody.toString());

            mailSender.send(message);
            log.info("Order receipt email sent successfully to: " + toEmail);
        } catch (Exception e) {
            log.error("Failed to send order receipt email: " + e.getMessage());
        }
    }
}