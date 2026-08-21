package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MemberWorkoutPlanDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.MemberWorkoutPlan;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Trainer;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.WorkoutPlan;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PlanStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberWorkoutPlanRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.TrainerRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.WorkoutPlanRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.MemberWorkoutPlanService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class MemberWorkoutPlanServiceImpl implements MemberWorkoutPlanService {
    private final MemberWorkoutPlanRepository memberWorkoutPlanRepository;
    private final MemberRepository memberRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final TrainerRepository trainerRepository;

    public MemberWorkoutPlanServiceImpl(MemberWorkoutPlanRepository memberWorkoutPlanRepository, MemberRepository memberRepository, WorkoutPlanRepository workoutPlanRepository, TrainerRepository trainerRepository) {
        this.memberWorkoutPlanRepository = memberWorkoutPlanRepository;
        this.memberRepository = memberRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public MemberWorkoutPlanDTO assignWorkoutPlan(MemberWorkoutPlanDTO dto) {
        log.info("Execute assignWorkoutPlan()");
        if (dto == null || dto.getMemberId() == null || dto.getPlanId() == null || dto.getTrainerId() == null) {
            return null;
        }

        Optional<Member> memberOpt = memberRepository.findById(dto.getMemberId());
        Optional<WorkoutPlan> planOpt = workoutPlanRepository.findById(dto.getPlanId());
        Optional<Trainer> trainerOpt = trainerRepository.findById(dto.getTrainerId());

        if (memberOpt.isEmpty() || planOpt.isEmpty() || trainerOpt.isEmpty()) {
            return null;
        }

        MemberWorkoutPlan mapEntity = new MemberWorkoutPlan();
        mapEntity.setAssignedDate(dto.getAssignedDate() != null ? dto.getAssignedDate() : LocalDate.now());
        mapEntity.setPlanStatus(PlanStatus.ACTIVE);
        mapEntity.setMember(memberOpt.get());
        mapEntity.setWorkoutPlan(planOpt.get());
        mapEntity.setTrainer(trainerOpt.get());

        MemberWorkoutPlan saved = memberWorkoutPlanRepository.save(mapEntity);
        log.info("Member workout plan assigned successfully!");

        return new MemberWorkoutPlanDTO(
                saved.getId(),
                saved.getAssignedDate(),
                saved.getPlanStatus(),
                saved.getMember().getMemberId(),
                saved.getWorkoutPlan().getPlanId(),
                saved.getTrainer().getTrainerId()
        );
    }

    @Override
    public MemberWorkoutPlanDTO updateMemberWorkoutPlan(MemberWorkoutPlanDTO dto) {
        log.info("Execute updateWorkoutPlan()");
        if (dto == null || dto.getId() == null) {
            return null;
        }

        Optional<MemberWorkoutPlan> optionalPlan = memberWorkoutPlanRepository.findById(dto.getId());
        if (optionalPlan.isEmpty()) {
            return null;
        }

        MemberWorkoutPlan map = optionalPlan.get();
        if (map.getPlanStatus() == PlanStatus.DELETED) {
            return null;
        }

        if (dto.getMemberId() != null) {
            Optional<Member> memberOpt = memberRepository.findById(dto.getMemberId());
            if (memberOpt.isPresent()) {
                map.setMember(memberOpt.get());
            } else {
                log.warn("Member not found with ID: {}", dto.getMemberId());
            }
        }

        if (dto.getPlanId() != null) {
            Optional<WorkoutPlan> planOpt = workoutPlanRepository.findById(dto.getPlanId());
            if (planOpt.isPresent()) {
                map.setWorkoutPlan(planOpt.get());
            } else {
                log.warn("Workout Plan not found with ID: {}", dto.getPlanId());
            }
        }

        if (dto.getTrainerId() != null) {
            Optional<Trainer> trainerOpt = trainerRepository.findById(dto.getTrainerId());
            if (trainerOpt.isPresent()) {
                map.setTrainer(trainerOpt.get());
            } else {
                log.warn("Trainer not found with ID: {}", dto.getTrainerId());
            }
        }

        if (dto.getAssignedDate() != null) {
            map.setAssignedDate(dto.getAssignedDate());
        }
        if (dto.getPlanStatus() != null) {
            map.setPlanStatus(dto.getPlanStatus());
        }

        MemberWorkoutPlan updated = memberWorkoutPlanRepository.save(map);
        log.info("Member workout plan updated successfully!");

        return new MemberWorkoutPlanDTO(
                updated.getId(),
                updated.getAssignedDate(),
                updated.getPlanStatus(),
                updated.getMember().getMemberId(),
                updated.getWorkoutPlan().getPlanId(),
                updated.getTrainer().getTrainerId()
        );
    }

    @Override
    public String deleteMemberWorkoutPlan(Long id) {
        log.info("Execute Soft Delete MemberWorkoutPlan()");
        if (id == null) return "Invalid ID!";

        Optional<MemberWorkoutPlan> optional = memberWorkoutPlanRepository.findById(id);
        if (optional.isEmpty()) {
            return "Assigned workout plan not found!";
        }

        MemberWorkoutPlan map = optional.get();
        if (map.getPlanStatus() == PlanStatus.DELETED) {
            return "Already deleted!";
        }

        map.setPlanStatus(PlanStatus.DELETED);
        memberWorkoutPlanRepository.save(map);

        log.info("Member workout plan marked as DELETED successfully!");
        return "Assigned workout plan deleted successfully!";
    }

    @Override
    public MemberWorkoutPlanDTO getMemberWorkoutPlanById(Long id) {
        log.info("Execute getMemberWorkoutPlanById()");
        if (id == null) return null;

        Optional<MemberWorkoutPlan> optional = memberWorkoutPlanRepository.findById(id);
        if (optional.isEmpty() || optional.get().getPlanStatus() == PlanStatus.DELETED) {
            return null;
        }

        MemberWorkoutPlan memWorkout = optional.get();
        return new MemberWorkoutPlanDTO(
                memWorkout.getId(),
                memWorkout.getAssignedDate(),
                memWorkout.getPlanStatus(),
                memWorkout.getMember().getMemberId(),
                memWorkout.getWorkoutPlan().getPlanId(),
                memWorkout.getTrainer().getTrainerId()
        );
    }

    @Override
    public List<MemberWorkoutPlanDTO> getAllMemberWorkoutPlans() {
        log.info("Execute getAllMemberWorkoutPlans()");
        List<MemberWorkoutPlan> list = memberWorkoutPlanRepository.findAll();
        List<MemberWorkoutPlanDTO> dtoList = new ArrayList<>();

        for (MemberWorkoutPlan memWorkout : list) {
            if (memWorkout.getPlanStatus() != PlanStatus.DELETED) {
                dtoList.add(new MemberWorkoutPlanDTO(
                        memWorkout.getId(),
                        memWorkout.getAssignedDate(),
                        memWorkout.getPlanStatus(),
                        memWorkout.getMember().getMemberId(),
                        memWorkout.getWorkoutPlan().getPlanId(),
                        memWorkout.getTrainer().getTrainerId()
                ));
            }
        }
        return dtoList;
    }
}