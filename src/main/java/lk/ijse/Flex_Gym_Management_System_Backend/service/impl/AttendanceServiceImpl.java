package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.AttendanceDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.AttendanceScanDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MonthlyAttendanceSummaryDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Attendance;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.AttendanceStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.AttendanceRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, MemberRepository memberRepository) {
        this.attendanceRepository = attendanceRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public AttendanceDTO markAttendance(AttendanceScanDTO attendanceScanDTO) {
        log.info("Execute markAttendance()");
        if (attendanceScanDTO == null) {
            throw new CustomException(400, "Attendance scan data cannot be null!");
        }
        if (attendanceScanDTO.getMemberId() == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }

        Optional<Member> optionalMember = memberRepository.findById(attendanceScanDTO.getMemberId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + attendanceScanDTO.getMemberId());
        }

        Member member = optionalMember.get();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // check already attendance marked for today
        List<Attendance> existingLogs = attendanceRepository.findAllByMember_MemberIdAndCheckInTimeBetween(
                member.getMemberId(), startOfDay, endOfDay
        );

        if (!existingLogs.isEmpty()) {
            Attendance existing = existingLogs.get(0);
            return convertToDTO(existing, "Attendance already marked for today!");
        }

        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setAttendanceStatus(AttendanceStatus.PRESENT);

        Attendance savedAttendance = attendanceRepository.save(attendance);
        log.info("Attendance marked successfully!");

        return convertToDTO(savedAttendance, "Attendance marked successfully! Welcome " + member.getMemberFullName());
    }

    @Override
    public List<AttendanceDTO> getAllAttendanceLogs() {
        log.info("Execute getAllAttendanceLogs()");
        List<Attendance> attendanceList = attendanceRepository.findAllByOrderByCheckInTimeDesc();
        List<AttendanceDTO> dtoList = new ArrayList<>();

        for (Attendance attendance : attendanceList) {
            dtoList.add(convertToDTO(attendance, "Success"));
        }
        return dtoList;
    }

    @Override
    public List<AttendanceDTO> getAttendanceByMemberId(Long memberId) {
        log.info("Execute getAttendanceByMemberId()");
        if (memberId == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }

        LocalDateTime startOfDay = LocalDate.now().minusYears(10).atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Attendance> attendanceList = attendanceRepository.findAllByMember_MemberIdAndCheckInTimeBetween(memberId, startOfDay, endOfDay);
        List<AttendanceDTO> dtoList = new ArrayList<>();

        for (Attendance attendance : attendanceList) {
            dtoList.add(convertToDTO(attendance, "Success"));
        }
        return dtoList;
    }

    @Override
    public MonthlyAttendanceSummaryDTO getMonthlyAttendanceSummary(Long memberId, int year, int month) {
        log.info("Execute getMonthlyAttendanceSummary()");
        if (memberId == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + memberId);
        }

        Member member = optionalMember.get();

        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.of(year, month, startOfMonth.toLocalDate().lengthOfMonth()).atTime(LocalTime.MAX);

        List<Attendance> monthlyLogs = attendanceRepository
                .findAllByMember_MemberIdAndCheckInTimeBetweenOrderByCheckInTimeDesc(memberId, startOfMonth, endOfMonth);

        List<AttendanceDTO> dtoList = new ArrayList<>();
        for (Attendance attendance : monthlyLogs) {
            dtoList.add(convertToDTO(attendance, "Success"));
        }

        MonthlyAttendanceSummaryDTO summaryDTO = new MonthlyAttendanceSummaryDTO();
        summaryDTO.setMemberId(member.getMemberId());
        summaryDTO.setMemberFullName(member.getMemberFullName());
        summaryDTO.setYear(year);
        summaryDTO.setMonth(month);
        summaryDTO.setTotalDaysAttended(dtoList.size());
        summaryDTO.setAttendanceLogs(dtoList);

        return summaryDTO;
    }

    private AttendanceDTO convertToDTO(Attendance attendance, String message) {
        AttendanceDTO dto = new AttendanceDTO();

        dto.setAttendanceId(attendance.getAttendanceId());
        dto.setMemberId(attendance.getMember().getMemberId());
        dto.setMemberFullName(attendance.getMember().getMemberFullName());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setAttendanceStatus(attendance.getAttendanceStatus());

        dto.setMessage(message);
        return dto;
    }
}