package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO placeOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByMemberId(Long memberId);
}