package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Payment;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByMember_MemberId(Long memberId);
    List<Payment> findAllByPaymentStatus(PaymentStatus paymentStatus);
    List<Payment> findAllByMember_MemberIdAndPaymentStatus(Long memberId, PaymentStatus paymentStatus);
}