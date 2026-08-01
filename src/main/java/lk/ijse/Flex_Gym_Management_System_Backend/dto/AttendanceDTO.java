package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long attendanceId;
    private Long memberId;
    private String memberFullName;
    private LocalDateTime checkInTime;
    private AttendanceStatus attendanceStatus;
    private String message;
}