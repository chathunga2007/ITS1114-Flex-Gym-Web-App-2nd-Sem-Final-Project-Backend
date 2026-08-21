package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public OrderServiceImpl(OrderRepository orderRepository, MemberRepository memberRepository, ProductRepository productRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public OrderDTO placeOrder(OrderDTO orderDTO) {
        log.info("Execute placeOrder()");
        if (orderDTO == null || orderDTO.getMemberId() == null || orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            return null;
        }

        Optional<Member> optionalMember = memberRepository.findById(orderDTO.getMemberId());
        if (optionalMember.isEmpty()) {
            return null;
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
            if (itemDTO == null || itemDTO.getProductId() == null || itemDTO.getQuantity() <= 0) {
                return null;
            }

            Optional<Product> optionalProduct = productRepository.findById(itemDTO.getProductId());
            if (optionalProduct.isEmpty() || optionalProduct.get().getProductStatus() == ProductStatus.DELETED) {
                return null;
            }

            Product product = optionalProduct.get();

            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                return null;
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

        return responseDTO;
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        if (orderId == null) {
            return null;
        }
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return null;
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
        if (memberId == null) {
            return new ArrayList<>();
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