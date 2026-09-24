package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.TrainerBooking;

@Repository
public interface TrainerBookingRepository extends JpaRepository<TrainerBooking, Long> {
    List<TrainerBooking> findByMember_MemberIdOrderBySessionDateDesc(Long memberId);
    List<TrainerBooking> findByTrainer_TrainerIdOrderBySessionDateDesc(Long trainerId);
    List<TrainerBooking> findByTrainer_TrainerIdAndSessionDate(Long trainerId, LocalDate sessionDate);
    List<TrainerBooking> findAllByOrderBySessionDateDesc();
}