package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.OrderDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "/placeOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse placeOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO savedOrder = orderService.placeOrder(orderDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedOrder, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getOrder(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return new CommonResponse(OPERATION_SUCCESS, orderDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllOrders() {
        List<OrderDTO> orderDTOList = orderService.getAllOrders();
        return new CommonResponse(OPERATION_SUCCESS, orderDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getMemberOrders/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMemberOrders(@PathVariable Long memberId) {
        List<OrderDTO> orderDTOList = orderService.getOrdersByMemberId(memberId);
        return new CommonResponse(OPERATION_SUCCESS, orderDTOList, SUCCESS_MESSAGE);
    }
}