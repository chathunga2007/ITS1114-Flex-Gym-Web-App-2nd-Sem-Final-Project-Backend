package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
    //check the marks the today member attendance
    List<Attendance> findAllByMember_MemberIdAndCheckInTimeBetween(Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Attendance> findAllByOrderByCheckInTimeDesc();
    List<Attendance> findAllByMember_MemberIdAndCheckInTimeBetweenOrderByCheckInTimeDesc(Long memberId, LocalDateTime startDate, LocalDateTime endDate);
}