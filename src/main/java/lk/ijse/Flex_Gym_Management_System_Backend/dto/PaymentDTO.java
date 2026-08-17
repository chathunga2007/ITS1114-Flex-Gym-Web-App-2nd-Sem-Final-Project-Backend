package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import java.math.BigDecimal;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private Long memberId;
    private String memberName;
}