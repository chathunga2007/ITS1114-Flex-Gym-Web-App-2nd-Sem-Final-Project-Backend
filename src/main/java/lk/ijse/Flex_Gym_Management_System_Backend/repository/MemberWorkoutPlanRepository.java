package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.MemberWorkoutPlan;

@Repository
public interface MemberWorkoutPlanRepository extends JpaRepository<MemberWorkoutPlan, Long> {
}