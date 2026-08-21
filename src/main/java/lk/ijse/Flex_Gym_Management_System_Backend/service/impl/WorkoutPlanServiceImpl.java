package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PlanStatus;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.WorkoutPlanDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.WorkoutPlan;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.WorkoutPlanRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.WorkoutPlanService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class WorkoutPlanServiceImpl implements WorkoutPlanService {
    private final WorkoutPlanRepository workoutPlanRepository;

    public WorkoutPlanServiceImpl(WorkoutPlanRepository workoutPlanRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
    }

    @Override
    public WorkoutPlanDTO saveWorkoutPlan(WorkoutPlanDTO dto) {
        log.info("Execute saveWorkoutPlan()");
        if (dto == null) {
            throw new CustomException(400, "Workout plan data cannot be null!");
        }
        if (dto.getPlanName() == null || dto.getPlanName().trim().isEmpty()) {
            throw new CustomException(400, "Workout plan name cannot be empty!");
        }

        WorkoutPlan plan = new WorkoutPlan();
        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setDifficultyLevelStatus(dto.getDifficultyLevelStatus());
        plan.setPlanStatus(PlanStatus.ACTIVE);

        WorkoutPlan savedPlan = workoutPlanRepository.save(plan);
        log.info("Workout plan saved successfully!");

        return new WorkoutPlanDTO(
                savedPlan.getPlanId(),
                savedPlan.getPlanName(),
                savedPlan.getDescription(),
                savedPlan.getDifficultyLevelStatus(),
                savedPlan.getPlanStatus()
        );
    }

    @Override
    public WorkoutPlanDTO updateWorkoutPlan(WorkoutPlanDTO dto) {
        log.info("Execute updateWorkoutPlan()");
        if (dto == null) {
            throw new CustomException(400, "Workout plan data cannot be null!");
        }
        if (dto.getPlanId() == null) {
            throw new CustomException(400, "Workout plan ID cannot be null for update!");
        }
        if (dto.getPlanName() == null || dto.getPlanName().trim().isEmpty()) {
            throw new CustomException(400, "Workout plan name cannot be empty!");
        }

        Optional<WorkoutPlan> optionalPlan = workoutPlanRepository.findById(dto.getPlanId());
        if (optionalPlan.isEmpty()) {
            throw new CustomException(404, "Workout plan not found with ID: " + dto.getPlanId());
        }

        WorkoutPlan plan = optionalPlan.get();
        if (plan.getPlanStatus() == PlanStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted workout plan!");
        }

        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setDifficultyLevelStatus(dto.getDifficultyLevelStatus());

        WorkoutPlan updatedPlan = workoutPlanRepository.save(plan);
        log.info("Workout plan updated successfully!");

        return new WorkoutPlanDTO(
                updatedPlan.getPlanId(),
                updatedPlan.getPlanName(),
                updatedPlan.getDescription(),
                updatedPlan.getDifficultyLevelStatus(),
                updatedPlan.getPlanStatus()
        );
    }

    @Override
    public String deleteWorkoutPlan(Long id) {
        log.info("Execute deleteWorkoutPlan()");
        if (id == null) {
            throw new CustomException(400, "Workout plan ID cannot be null!");
        }

        Optional<WorkoutPlan> optionalPlan = workoutPlanRepository.findById(id);
        if (optionalPlan.isEmpty()) {
            throw new CustomException(404, "Workout plan not found with ID: " + id);
        }

        WorkoutPlan plan = optionalPlan.get();
        if (plan.getPlanStatus() == PlanStatus.DELETED) {
            throw new CustomException(400, "Workout plan is already deleted!");
        }

        plan.setPlanStatus(PlanStatus.DELETED);
        workoutPlanRepository.save(plan);

        log.info("Workout plan marked as DELETED successfully!");
        return "Workout plan deleted successfully!";
    }

    @Override
    public WorkoutPlanDTO getWorkoutPlanById(Long id) {
        log.info("Execute getWorkoutPlanById()");
        if (id == null) {
            throw new CustomException(400, "Workout plan ID cannot be null!");
        }

        Optional<WorkoutPlan> optionalPlan = workoutPlanRepository.findById(id);
        if (optionalPlan.isEmpty()) {
            throw new CustomException(404, "Workout plan not found with ID: " + id);
        }

        WorkoutPlan plan = optionalPlan.get();
        if (plan.getPlanStatus() == PlanStatus.DELETED) {
            throw new CustomException(404, "Workout plan not found with ID: " + id);
        }

        return new WorkoutPlanDTO(
                plan.getPlanId(),
                plan.getPlanName(),
                plan.getDescription(),
                plan.getDifficultyLevelStatus(),
                plan.getPlanStatus()
        );
    }

    @Override
    public List<WorkoutPlanDTO> getAllWorkoutPlans() {
        log.info("Execute getAllWorkoutPlans()");
        List<WorkoutPlan> plans = workoutPlanRepository.findAll();
        List<WorkoutPlanDTO> dtoList = new ArrayList<>();

        for (WorkoutPlan plan : plans) {
            if (plan.getPlanStatus() != PlanStatus.DELETED) {
                dtoList.add(new WorkoutPlanDTO(
                        plan.getPlanId(),
                        plan.getPlanName(),
                        plan.getDescription(),
                        plan.getDifficultyLevelStatus(),
                        plan.getPlanStatus()
                ));
            }
        }
        return dtoList;
    }
}