package lk.ijse.Flex_Gym_Management_System_Backend.service;

import java.util.List;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.WorkoutPlanDTO;

public interface WorkoutPlanService {
    WorkoutPlanDTO saveWorkoutPlan(WorkoutPlanDTO workoutPlanDTO);
    WorkoutPlanDTO updateWorkoutPlan(WorkoutPlanDTO workoutPlanDTO);
    String deleteWorkoutPlan(Long id);
    WorkoutPlanDTO getWorkoutPlanById(Long id);
    List<WorkoutPlanDTO> getAllWorkoutPlans();
}