package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.OrderDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.OrderItemDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Order;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.OrderItem;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Payment;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Product;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.OrderStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentType;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.ProductStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.OrderRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.PaymentRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.ProductRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.OrderService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    public OrderServiceImpl(OrderRepository orderRepository, MemberRepository memberRepository, ProductRepository productRepository, PaymentRepository paymentRepository, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
    }

    @Override
    public OrderDTO placeOrder(OrderDTO orderDTO) {
        log.info("Execute placeOrder()");
        if (orderDTO == null) {
            throw new CustomException(400, "Order data cannot be null!");
        }
        if (orderDTO.getMemberId() == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }
        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            throw new CustomException(400, "Order items cannot be empty!");
        }

        Optional<Member> optionalMember = memberRepository.findById(orderDTO.getMemberId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + orderDTO.getMemberId());
        }

        Member member = optionalMember.get();

        Order order = new Order();
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.COMPLETED);

        PaymentStatus paymentStatus = orderDTO.getPaymentStatus() != null ? orderDTO.getPaymentStatus() : PaymentStatus.PAID;
        order.setPaymentStatus(paymentStatus);

        BigDecimal calculatedTotal = BigDecimal.ZERO;
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            if (itemDTO == null || itemDTO.getProductId() == null) {
                throw new CustomException(400, "Product ID cannot be null in order items!");
            }
            if (itemDTO.getQuantity() <= 0) {
                throw new CustomException(400, "Order item quantity must be greater than zero!");
            }

            Optional<Product> optionalProduct = productRepository.findById(itemDTO.getProductId());
            if (optionalProduct.isEmpty()) {
                throw new CustomException(404, "Product not found with ID: " + itemDTO.getProductId());
            }

            Product product = optionalProduct.get();
            if (product.getProductStatus() == ProductStatus.DELETED) {
                throw new CustomException(400, "Cannot order a deleted product: " + product.getProductName());
            }

            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                throw new CustomException(400, "Insufficient stock for product: " + product.getProductName() + ". Available stock: " + product.getStockQuantity());
            }

            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());

            BigDecimal unitPrice = itemDTO.getUnitPrice() != null ? itemDTO.getUnitPrice() : product.getProductPrice();
            orderItem.setUnitPrice(unitPrice);

            calculatedTotal = calculatedTotal.add(unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItemList.add(orderItem);
        }

        order.setTotalAmount(calculatedTotal);
        order.setOrderItems(orderItemList);

        Order savedOrder = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setAmount(savedOrder.getTotalAmount());
        payment.setPaymentType(PaymentType.SHOP_ORDER);
        payment.setPaymentStatus(savedOrder.getPaymentStatus());
        payment.setMember(member);
        paymentRepository.save(payment);
        log.info("Automatic Shop Order Payment saved successfully!");

        OrderDTO responseDTO = new OrderDTO();
        responseDTO.setOrderId(savedOrder.getOrderId());
        responseDTO.setMemberId(savedOrder.getMember().getMemberId());
        responseDTO.setMemberFullName(savedOrder.getMember().getMemberFullName());
        responseDTO.setTotalAmount(savedOrder.getTotalAmount());
        responseDTO.setOrderDate(savedOrder.getOrderDate());
        responseDTO.setOrderStatus(savedOrder.getOrderStatus());
        responseDTO.setPaymentStatus(savedOrder.getPaymentStatus());

        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (savedOrder.getOrderItems() != null) {
            for (OrderItem item : savedOrder.getOrderItems()) {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setOrderItemId(item.getOrderItemId());
                itemDTO.setProductId(item.getProduct().getProductId());
                itemDTO.setProductName(item.getProduct().getProductName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setUnitPrice(item.getUnitPrice());
                itemDTOs.add(itemDTO);
            }
        }
        responseDTO.setItems(itemDTOs);

        try {
            String memberEmail = null;
            if (member.getUser() != null) {
                memberEmail = member.getUser().getEmail();
            }

            if (memberEmail != null && !memberEmail.isBlank()) {
                emailService.sendOrderReceiptEmail(
                        memberEmail,
                        member.getMemberFullName(),
                        savedOrder.getOrderId(),
                        savedOrder.getTotalAmount(),
                        itemDTOs
                );
                log.info("Order receipt email triggered successfully for member: " + member.getMemberFullName());
            }
        } catch (Exception e) {
            log.error("Failed to send order receipt email: " + e.getMessage());
        }

        return responseDTO;
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        log.info("Execute getOrderById()");
        if (orderId == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new CustomException(404, "Order not found with ID: " + orderId);
        }
        Order order = optionalOrder.get();

        OrderDTO responseDTO = new OrderDTO();
        responseDTO.setOrderId(order.getOrderId());
        responseDTO.setMemberId(order.getMember().getMemberId());
        responseDTO.setMemberFullName(order.getMember().getMemberFullName());
        responseDTO.setTotalAmount(order.getTotalAmount());
        responseDTO.setOrderDate(order.getOrderDate());
        responseDTO.setOrderStatus(order.getOrderStatus());
        responseDTO.setPaymentStatus(order.getPaymentStatus());

        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setOrderItemId(item.getOrderItemId());
                itemDTO.setProductId(item.getProduct().getProductId());
                itemDTO.setProductName(item.getProduct().getProductName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setUnitPrice(item.getUnitPrice());
                itemDTOs.add(itemDTO);
            }
        }
        responseDTO.setItems(itemDTOs);

        return responseDTO;
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        log.info("Execute getAllOrders()");
        List<Order> orderList = orderRepository.findAllByOrderByOrderDateDesc();
        List<OrderDTO> dtoList = new ArrayList<>();

        for (Order order : orderList) {
            OrderDTO responseDTO = new OrderDTO();
            responseDTO.setOrderId(order.getOrderId());
            responseDTO.setMemberId(order.getMember().getMemberId());
            responseDTO.setMemberFullName(order.getMember().getMemberFullName());
            responseDTO.setTotalAmount(order.getTotalAmount());
            responseDTO.setOrderDate(order.getOrderDate());
            responseDTO.setOrderStatus(order.getOrderStatus());
            responseDTO.setPaymentStatus(order.getPaymentStatus());

            List<OrderItemDTO> itemDTOs = new ArrayList<>();
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setOrderItemId(item.getOrderItemId());
                    itemDTO.setProductId(item.getProduct().getProductId());
                    itemDTO.setProductName(item.getProduct().getProductName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setUnitPrice(item.getUnitPrice());
                    itemDTOs.add(itemDTO);
                }
            }
            responseDTO.setItems(itemDTOs);
            dtoList.add(responseDTO);
        }
        return dtoList;
    }

    @Override
    public List<OrderDTO> getOrdersByMemberId(Long memberId) {
        log.info("Execute getOrdersByMemberId()");
        if (memberId == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }
        List<Order> orderList = orderRepository.findAllByMember_MemberIdOrderByOrderDateDesc(memberId);
        List<OrderDTO> dtoList = new ArrayList<>();

        for (Order order : orderList) {
            OrderDTO responseDTO = new OrderDTO();
            responseDTO.setOrderId(order.getOrderId());
            responseDTO.setMemberId(order.getMember().getMemberId());
            responseDTO.setMemberFullName(order.getMember().getMemberFullName());
            responseDTO.setTotalAmount(order.getTotalAmount());
            responseDTO.setOrderDate(order.getOrderDate());
            responseDTO.setOrderStatus(order.getOrderStatus());
            responseDTO.setPaymentStatus(order.getPaymentStatus());

            List<OrderItemDTO> itemDTOs = new ArrayList<>();
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setOrderItemId(item.getOrderItemId());
                    itemDTO.setProductId(item.getProduct().getProductId());
                    itemDTO.setProductName(item.getProduct().getProductName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setUnitPrice(item.getUnitPrice());
                    itemDTOs.add(itemDTO);
                }
            }
            responseDTO.setItems(itemDTOs);
            dtoList.add(responseDTO);
        }
        return dtoList;
    }
}