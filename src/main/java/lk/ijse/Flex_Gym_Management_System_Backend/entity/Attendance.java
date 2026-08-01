package lk.ijse.Flex_Gym_Management_System_Backend.entity;

import jakarta.persistence.*;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.AttendanceStatus;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;
    private LocalDateTime checkInTime = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}