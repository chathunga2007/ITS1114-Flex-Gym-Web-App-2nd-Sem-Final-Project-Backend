package lk.ijse.Flex_Gym_Management_System_Backend.entity;

import jakarta.persistence.*;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PlanDifficulty;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workout_plans")
public class WorkoutPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;
    private String planName;
    private String description;
    @Enumerated(EnumType.STRING)
    private PlanDifficulty difficultyLevelStatus;
    @Enumerated(EnumType.STRING)
    private PlanStatus planStatus = PlanStatus.ACTIVE;
}