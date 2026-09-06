package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import jakarta.mail.internet.MimeMessage;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.OrderItemDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
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
}