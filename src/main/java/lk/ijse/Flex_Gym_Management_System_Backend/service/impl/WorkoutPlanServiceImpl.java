package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PlanStatus;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.WorkoutPlanDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.WorkoutPlan;
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
        if (dto == null || dto.getPlanName() == null) {
            return null;
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
        if (dto == null || dto.getPlanId() == null) {
            return null;
        }

        Optional<WorkoutPlan> optionalPlan = workoutPlanRepository.findById(dto.getPlanId());
        if (optionalPlan.isEmpty()) {
            return null;
        }

        WorkoutPlan plan = optionalPlan.get();
        if (plan.getPlanStatus() == PlanStatus.DELETED) {
            return null;
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
        log.info("Execute Soft Delete WorkoutPlan()");
        if (id == null) return "Invalid ID!";

        Optional<WorkoutPlan> optionalPlan = workoutPlanRepository.findById(id);
        if (optionalPlan.isEmpty()) {
            return "Workout plan not found!";
        }

        WorkoutPlan plan = optionalPlan.get();
        if (plan.getPlanStatus() == PlanStatus.DELETED) {
            return "Workout plan is already deleted!";
        }

        plan.setPlanStatus(PlanStatus.DELETED);
        workoutPlanRepository.save(plan);

        log.info("Workout plan marked as DELETED successfully!");
        return "Workout plan deleted successfully!";
    }

    @Override
    public WorkoutPlanDTO getWorkoutPlanById(Long id) {
        log.info("Execute getWorkoutPlanById()");
        if (id == null) return null;

        Optional<WorkoutPlan> optionalPlan = workoutPlanRepository.findById(id);
        if (optionalPlan.isEmpty()) {
            log.error("Workout plan not found!");
        };

        WorkoutPlan plan = optionalPlan.get();
        if (plan.getPlanStatus() == PlanStatus.DELETED) {
            return null;
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