package lk.ijse.Flex_Gym_Management_System_Backend.service;

public interface EmailService {
    void sendAccountCredentialsEmail(String toEmail, String name, String password);
}