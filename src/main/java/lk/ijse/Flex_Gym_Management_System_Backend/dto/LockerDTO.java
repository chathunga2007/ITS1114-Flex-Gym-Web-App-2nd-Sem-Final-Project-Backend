package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.LockerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockerDTO {
    private Long lockerId;
    private String lockerNumber;
    private Boolean isOccupied;
    private LockerStatus status;
    private Long memberId;
}