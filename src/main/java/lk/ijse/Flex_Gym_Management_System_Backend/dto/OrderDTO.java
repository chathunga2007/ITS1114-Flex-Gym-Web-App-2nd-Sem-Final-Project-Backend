package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.OrderStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Long memberId;
    private String memberFullName;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private List<OrderItemDTO> items;
}