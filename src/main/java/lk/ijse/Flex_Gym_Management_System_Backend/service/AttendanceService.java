package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.AttendanceDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.AttendanceScanDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MonthlyAttendanceSummaryDTO;
import java.util.List;

public interface AttendanceService {
    AttendanceDTO markAttendance(AttendanceScanDTO attendanceScanDTO);
    List<AttendanceDTO> getAllAttendanceLogs();
    List<AttendanceDTO> getAttendanceByMemberId(Long memberId);
    // monthly attendance summary check
    MonthlyAttendanceSummaryDTO getMonthlyAttendanceSummary(Long memberId, int year, int month);
}