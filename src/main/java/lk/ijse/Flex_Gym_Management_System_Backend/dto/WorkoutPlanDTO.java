package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PlanDifficulty;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutPlanDTO {
    private Long planId;
    private String planName;
    private String description;
    private PlanDifficulty difficultyLevelStatus;
    private PlanStatus planStatus;
}