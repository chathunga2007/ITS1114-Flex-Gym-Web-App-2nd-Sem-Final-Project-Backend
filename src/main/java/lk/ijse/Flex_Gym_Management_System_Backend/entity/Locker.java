package lk.ijse.Flex_Gym_Management_System_Backend.entity;

import jakarta.persistence.*;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.LockerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lockers")
public class Locker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lockerId;
    private String lockerNumber;
    private Boolean isOccupied = false;
    @Enumerated(EnumType.STRING)
    private LockerStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;
}