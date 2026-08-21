package lk.ijse.Flex_Gym_Management_System_Backend.service;

import java.util.List;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MemberWorkoutPlanDTO;

public interface MemberWorkoutPlanService {
    MemberWorkoutPlanDTO assignWorkoutPlan(MemberWorkoutPlanDTO dto);
    MemberWorkoutPlanDTO updateMemberWorkoutPlan(MemberWorkoutPlanDTO dto);
    String deleteMemberWorkoutPlan(Long id);
    MemberWorkoutPlanDTO getMemberWorkoutPlanById(Long id);
    List<MemberWorkoutPlanDTO> getAllMemberWorkoutPlans();
}