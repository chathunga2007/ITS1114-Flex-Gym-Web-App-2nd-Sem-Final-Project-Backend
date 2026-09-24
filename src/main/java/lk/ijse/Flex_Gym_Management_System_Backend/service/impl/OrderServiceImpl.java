package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentMethod;
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

        PaymentMethod paymentMethod = orderDTO.getPaymentMethod() != null ? orderDTO.getPaymentMethod() : PaymentMethod.CASH_ON_DELIVERY;
        order.setPaymentMethod(paymentMethod);

        PaymentStatus paymentStatus;
        OrderStatus orderStatus;

        if (paymentMethod == PaymentMethod.CREDIT_CARD) {
            paymentStatus = PaymentStatus.PAID;
            orderStatus = OrderStatus.CONFIRMED;
        } else {
            paymentStatus = orderDTO.getPaymentStatus() != null ? orderDTO.getPaymentStatus() : PaymentStatus.PENDING;
            orderStatus = orderDTO.getOrderStatus() != null ? orderDTO.getOrderStatus() : OrderStatus.PENDING;
        }

        order.setPaymentStatus(paymentStatus);
        order.setOrderStatus(orderStatus);

        order.setShippingAddress(orderDTO.getShippingAddress() != null ? orderDTO.getShippingAddress() : member.getMemberFullName() + " Address");
        order.setDeliveryCity(orderDTO.getDeliveryCity() != null ? orderDTO.getDeliveryCity() : "Colombo");
        order.setPostalCode(orderDTO.getPostalCode() != null ? orderDTO.getPostalCode() : "00300");
        order.setContactPhone(orderDTO.getContactPhone() != null ? orderDTO.getContactPhone() : member.getMemberPhoneNumber());
        order.setOrderNotes(orderDTO.getOrderNotes());
        order.setCardLast4(orderDTO.getCardLast4());
        order.setCardBrand(orderDTO.getCardBrand());

        // Generate tracking number e.g., FLX-TRK-784920
        int randSuffix = 100000 + new Random().nextInt(900000);
        String trackingNumber = "FLX-TRK-" + randSuffix;
        order.setTrackingNumber(trackingNumber);
        order.setCourierName(orderDTO.getCourierName() != null ? orderDTO.getCourierName() : "Flex Express Logistics");
        order.setEstimatedDeliveryDate(LocalDate.now().plusDays(3));

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

        OrderDTO responseDTO = mapToDTO(savedOrder);

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
                        responseDTO.getItems()
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
        return mapToDTO(optionalOrder.get());
    }

    @Override
    public OrderDTO getOrderByTrackingNumber(String trackingNumber) {
        log.info("Execute getOrderByTrackingNumber() for tracking: {}", trackingNumber);
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new CustomException(400, "Tracking number cannot be empty!");
        }
        Optional<Order> optionalOrder = orderRepository.findByTrackingNumber(trackingNumber.trim());
        if (optionalOrder.isEmpty()) {
            throw new CustomException(404, "No order found with Tracking Number: " + trackingNumber);
        }
        return mapToDTO(optionalOrder.get());
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        log.info("Execute getAllOrders()");
        List<Order> orderList = orderRepository.findAllByOrderByOrderDateDesc();
        List<OrderDTO> dtoList = new ArrayList<>();
        for (Order order : orderList) {
            dtoList.add(mapToDTO(order));
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
            dtoList.add(mapToDTO(order));
        }
        return dtoList;
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        return updateOrderStatus(orderId, orderStatus, paymentStatus, null, null);
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus orderStatus, PaymentStatus paymentStatus, String courierName, String trackingNumber) {
        log.info("Execute updateOrderStatus() for orderId: {}", orderId);
        if (orderId == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new CustomException(404, "Order not found with ID: " + orderId);
        }
        Order order = optionalOrder.get();
        OrderStatus oldStatus = order.getOrderStatus();

        if (orderStatus != null) {
            order.setOrderStatus(orderStatus);
        }
        if (courierName != null && !courierName.isBlank()) {
            order.setCourierName(courierName);
        }
        if (trackingNumber != null && !trackingNumber.isBlank()) {
            order.setTrackingNumber(trackingNumber);
        }
        if (paymentStatus != null) {
            order.setPaymentStatus(paymentStatus);
            if (paymentStatus == PaymentStatus.PAID && order.getMember() != null) {
                List<Payment> pendingPayments = paymentRepository.findAllByMember_MemberIdAndPaymentStatus(order.getMember().getMemberId(), PaymentStatus.PENDING);
                for (Payment p : pendingPayments) {
                    if (p.getPaymentType() == PaymentType.SHOP_ORDER) {
                        p.setPaymentStatus(PaymentStatus.PAID);
                        paymentRepository.save(p);
                        break;
                    }
                }
            }
        }

        Order updatedOrder = orderRepository.save(order);

        // Send email notifications on key status updates
        try {
            String memberEmail = (updatedOrder.getMember() != null && updatedOrder.getMember().getUser() != null)
                    ? updatedOrder.getMember().getUser().getEmail() : null;
            String memberName = updatedOrder.getMember() != null ? updatedOrder.getMember().getMemberFullName() : "Valued Member";

            if (memberEmail != null && !memberEmail.isBlank()) {
                if (orderStatus == OrderStatus.SHIPPED && oldStatus != OrderStatus.SHIPPED) {
                    emailService.sendOrderDispatchedEmail(
                            memberEmail,
                            memberName,
                            updatedOrder.getOrderId(),
                            updatedOrder.getTrackingNumber(),
                            updatedOrder.getCourierName(),
                            updatedOrder.getEstimatedDeliveryDate() != null ? updatedOrder.getEstimatedDeliveryDate().toString() : "2-4 Business Days"
                    );
                } else if ((orderStatus == OrderStatus.DELIVERED || orderStatus == OrderStatus.COMPLETED)
                        && oldStatus != OrderStatus.DELIVERED && oldStatus != OrderStatus.COMPLETED) {
                    if (updatedOrder.getTrackingNumber() != null && !updatedOrder.getTrackingNumber().isBlank()
                            && updatedOrder.getCourierName() != null && !updatedOrder.getCourierName().isBlank()) {
                        emailService.sendOrderDeliveredEmail(
                                memberEmail,
                                memberName,
                                updatedOrder.getOrderId(),
                                updatedOrder.getTrackingNumber(),
                                updatedOrder.getCourierName()
                        );
                    } else {
                        emailService.sendOrderDeliveredEmail(
                                memberEmail,
                                memberName,
                                updatedOrder.getOrderId()
                        );
                    }
                    log.info("Order delivered confirmation email triggered successfully for order #{}", updatedOrder.getOrderId());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send order status transition email: {}", e.getMessage());
        }

        return mapToDTO(updatedOrder);
    }

    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setMemberId(order.getMember() != null ? order.getMember().getMemberId() : null);
        dto.setMemberFullName(order.getMember() != null ? order.getMember().getMemberFullName() : "Guest");
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentMethod(order.getPaymentMethod());

        dto.setShippingAddress(order.getShippingAddress());
        dto.setDeliveryCity(order.getDeliveryCity());
        dto.setPostalCode(order.getPostalCode());
        dto.setContactPhone(order.getContactPhone());
        dto.setOrderNotes(order.getOrderNotes());

        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setCourierName(order.getCourierName());
        dto.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());

        dto.setCardLast4(order.getCardLast4());
        dto.setCardBrand(order.getCardBrand());

        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setOrderItemId(item.getOrderItemId());
                if (item.getProduct() != null) {
                    itemDTO.setProductId(item.getProduct().getProductId());
                    itemDTO.setProductName(item.getProduct().getProductName());
                }
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setUnitPrice(item.getUnitPrice());
                itemDTOs.add(itemDTO);
            }
        }
        dto.setItems(itemDTOs);
        return dto;
    }
}