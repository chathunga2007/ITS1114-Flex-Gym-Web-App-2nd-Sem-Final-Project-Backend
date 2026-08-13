package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.TrainerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerDTO {
    private Long trainerId;
    private String trainerName;
    private String specialization;
    private String phoneNumber;
    private String email;
    private TrainerStatus status;
}