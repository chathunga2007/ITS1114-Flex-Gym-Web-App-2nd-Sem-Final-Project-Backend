package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
}