package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.PaymentDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentStatus;
import java.util.List;

public interface PaymentService {
    PaymentDTO savePayment(PaymentDTO paymentDTO);
    PaymentDTO updatePaymentStatus(Long id, PaymentStatus paymentStatus);
    PaymentDTO getPaymentById(Long id);
    List<PaymentDTO> getAllPayments();
    List<PaymentDTO> getPaymentsByMemberId(Long memberId);
    String deletePayment(Long id);
}