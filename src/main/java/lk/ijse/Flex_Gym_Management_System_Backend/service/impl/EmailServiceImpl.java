package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import jakarta.mail.internet.MimeMessage;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.OrderItemDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
        log.info("Sending HTML credentials email with external CSS to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Welcome to Flex Gym - Your Account Credentials");

            ClassPathResource cssResource = new ClassPathResource("css/style.css");
            String cssContent = new String(cssResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            ClassPathResource htmlResource = new ClassPathResource("html/credentials-email.html");
            String htmlContent = new String(htmlResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[[styleContent]]", cssContent);

            htmlContent = htmlContent.replace("[[name]]", name)
                    .replace("[[email]]", toEmail)
                    .replace("[[password]]", password);

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

            log.info("HTML Credentials email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send HTML credentials email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendOrderReceiptEmail(String toEmail, String memberName, Long orderId, BigDecimal totalAmount, List<OrderItemDTO> items) {
        log.info("Sending HTML order receipt email with external CSS to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Flex Gym - Order Receipt #" + orderId);

            ClassPathResource cssResource = new ClassPathResource("css/style.css");
            String cssContent = new String(cssResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            ClassPathResource htmlResource = new ClassPathResource("html/order-receipt.html");
            String htmlContent = new String(htmlResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            StringBuilder rowsBuilder = new StringBuilder();
            if (items != null) {
                for (OrderItemDTO item : items) {
                    rowsBuilder.append("<tr>")
                            .append("<td>").append(item.getProductName()).append("</td>")
                            .append("<td>").append(item.getQuantity()).append("</td>")
                            .append("<td>Rs. ").append(item.getUnitPrice()).append("</td>")
                            .append("</tr>");
                }
            }

            htmlContent = htmlContent.replace("[[styleContent]]", cssContent);

            htmlContent = htmlContent.replace("[[memberName]]", memberName)
                    .replace("[[orderId]]", String.valueOf(orderId))
                    .replace("[[totalAmount]]", String.valueOf(totalAmount))
                    .replace("[[tableRows]]", rowsBuilder.toString());

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

            log.info("HTML Order receipt email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send HTML order receipt email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendMembershipExpiryReminderEmail(String toEmail, String memberName, String packageName, String expiryDate, int daysRemaining) {
        log.info("Sending membership expiry reminder email to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Flex Gym - Membership Expiry Reminder (in " + daysRemaining + " days)");

            ClassPathResource cssResource = new ClassPathResource("css/style.css");
            String cssContent = new String(cssResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            ClassPathResource htmlResource = new ClassPathResource("html/membership-reminder-email.html");
            String htmlContent = new String(htmlResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[[styleContent]]", cssContent)
                    .replace("[[memberName]]", memberName)
                    .replace("[[packageName]]", packageName)
                    .replace("[[expiryDate]]", expiryDate)
                    .replace("[[daysRemaining]]", String.valueOf(daysRemaining));

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

            log.info("Membership expiry reminder email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send membership expiry reminder email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendMembershipExpiredEmail(String toEmail, String memberName, String packageName, String expiredDate) {
        log.info("Sending membership expired notification email to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Flex Gym - Your Membership Has Expired");

            ClassPathResource cssResource = new ClassPathResource("css/style.css");
            String cssContent = new String(cssResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            ClassPathResource htmlResource = new ClassPathResource("html/membership-expired-email.html");
            String htmlContent = new String(htmlResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[[styleContent]]", cssContent)
                    .replace("[[memberName]]", memberName)
                    .replace("[[packageName]]", packageName)
                    .replace("[[expiredDate]]", expiredDate);

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

            log.info("Membership expired notification email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send membership expired email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendOrderDispatchedEmail(String toEmail, String memberName, Long orderId, String trackingNumber, String courierName, String estimatedDelivery) {
        log.info("Sending order dispatched email to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Flex Gym - Order #" + orderId + " Dispatched (" + trackingNumber + ")");

            ClassPathResource cssResource = new ClassPathResource("css/style.css");
            String cssContent = "";
            try {
                cssContent = new String(cssResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {}

            ClassPathResource htmlResource = new ClassPathResource("html/order-dispatched.html");
            String htmlContent = new String(htmlResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[[styleContent]]", cssContent)
                    .replace("[[memberName]]", memberName != null ? memberName : "Customer")
                    .replace("[[orderId]]", String.valueOf(orderId))
                    .replace("[[trackingNumber]]", trackingNumber != null ? trackingNumber : "N/A")
                    .replace("[[courierName]]", courierName != null ? courierName : "Flex Express Logistics")
                    .replace("[[estimatedDelivery]]", estimatedDelivery != null ? estimatedDelivery : "2-4 Business Days");

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Order dispatched email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send order dispatched email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendOrderDeliveredEmail(String toEmail, String memberName, Long orderId) {
        sendOrderDeliveredEmail(toEmail, memberName, orderId, "FLX-TRK-" + orderId + "920", "Flex Express Logistics");
    }

    @Override
    public void sendOrderDeliveredEmail(String toEmail, String memberName, Long orderId, String trackingNumber, String courierName) {
        log.info("Sending styled order delivered HTML email to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Flex Gym - Order #" + orderId + " Delivered Successfully! 🎉");

            ClassPathResource cssResource = new ClassPathResource("css/style.css");
            String cssContent = "";
            try {
                cssContent = new String(cssResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {}

            ClassPathResource htmlResource = new ClassPathResource("html/order-delivered.html");
            String htmlContent = new String(htmlResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[[styleContent]]", cssContent)
                    .replace("[[memberName]]", memberName != null ? memberName : "Valued Member")
                    .replace("[[orderId]]", String.valueOf(orderId))
                    .replace("[[trackingNumber]]", trackingNumber != null ? trackingNumber : ("FLX-TRK-" + orderId + "920"))
                    .replace("[[courierName]]", courierName != null ? courierName : "Flex Express Logistics");

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Styled order delivered HTML email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send styled order delivered email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String memberName) {
        log.info("Sending welcome email to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Welcome to Flex Gym! Train Hard. Live Strong.");

            ClassPathResource cssResource = new ClassPathResource("css/style.css");
            String cssContent = "";
            try {
                cssContent = new String(cssResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {}

            ClassPathResource htmlResource = new ClassPathResource("html/welcome-email.html");
            String htmlContent = new String(htmlResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[[styleContent]]", cssContent)
                    .replace("[[memberName]]", memberName != null ? memberName : "Member");

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendBookingConfirmationEmail(String toEmail, String memberName, String trainerName, String sessionDate, String timeSlot, String focusArea) {
        log.info("Sending PT booking confirmation HTML email to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Flex Gym - PT Session Confirmed with " + (trainerName != null ? trainerName : "Your Coach") + " 🏋️‍♂️");

            ClassPathResource htmlResource = new ClassPathResource("html/booking-confirmation.html");
            String htmlContent = new String(htmlResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[[memberName]]", memberName != null ? memberName : "Valued Member")
                    .replace("[[trainerName]]", trainerName != null ? trainerName : "Certified Coach")
                    .replace("[[sessionDate]]", sessionDate != null ? sessionDate : "Scheduled Date")
                    .replace("[[timeSlot]]", timeSlot != null ? timeSlot : "Reserved Slot")
                    .replace("[[focusArea]]", focusArea != null ? focusArea : "Full Body Fitness");

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("PT booking confirmation email successfully sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send PT booking confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Flex Gym - Password Reset OTP");
            message.setText("Your OTP for resetting your Flex Gym account password is: " + otp
                    + "\n\nThis OTP is valid for 5 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new CustomException(500, "Failed to send OTP email: " + e.getMessage());
        }
    }
}