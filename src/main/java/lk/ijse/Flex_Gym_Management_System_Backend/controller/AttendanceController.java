package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.AttendanceDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.AttendanceScanDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MonthlyAttendanceSummaryDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.AttendanceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping(value = "/scan", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse markAttendance(@RequestBody AttendanceScanDTO attendanceScanDTO) {
        AttendanceDTO attendanceDTO = attendanceService.markAttendance(attendanceScanDTO);
        return new CommonResponse(OPERATION_SUCCESS, attendanceDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllLogs", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllAttendanceLogs() {
        List<AttendanceDTO> dtoList = attendanceService.getAllAttendanceLogs();
        return new CommonResponse(OPERATION_SUCCESS, dtoList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getMemberAttendance/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMemberAttendance(@PathVariable Long memberId) {
        List<AttendanceDTO> dtoList = attendanceService.getAttendanceByMemberId(memberId);
        return new CommonResponse(OPERATION_SUCCESS, dtoList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getMonthlySummary/{memberId}/{year}/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMonthlySummary(@PathVariable Long memberId, @PathVariable int year, @PathVariable int month) {
        MonthlyAttendanceSummaryDTO summaryDTO = attendanceService.getMonthlyAttendanceSummary(memberId, year, month);
        return new CommonResponse(OPERATION_SUCCESS, summaryDTO, SUCCESS_MESSAGE);
    }
}