package lk.ijse.Flex_Gym_Management_System_Backend.entity;

import jakarta.persistence.*;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.UserRole;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.UserStatus;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Member member;
}