package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAttendanceSummaryDTO {
    private Long memberId;
    private String memberFullName;
    private Integer year;
    private Integer month;
    private Integer totalDaysAttended;
    private List<AttendanceDTO> attendanceLogs;
}