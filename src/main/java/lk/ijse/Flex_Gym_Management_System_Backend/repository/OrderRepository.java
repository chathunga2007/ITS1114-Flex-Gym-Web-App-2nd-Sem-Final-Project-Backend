package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findAllByMember_MemberIdOrderByOrderDateDesc(Long memberId);
    List<Order> findAllByOrderByOrderDateDesc();
    Optional<Order> findByTrackingNumber(String trackingNumber);
}