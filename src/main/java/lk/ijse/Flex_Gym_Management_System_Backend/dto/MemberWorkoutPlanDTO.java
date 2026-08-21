package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberWorkoutPlanDTO {
    private Long id;
    private LocalDate assignedDate;
    private PlanStatus planStatus;
    private Long memberId;
    private Long planId;
    private Long trainerId;
}