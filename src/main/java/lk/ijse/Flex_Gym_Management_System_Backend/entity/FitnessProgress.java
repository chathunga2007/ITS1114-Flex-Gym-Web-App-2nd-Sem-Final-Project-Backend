package lk.ijse.Flex_Gym_Management_System_Backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fitness_progress")
public class FitnessProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(precision = 6, scale = 2, nullable = false)
    private BigDecimal weightKg;

    @Column(precision = 5, scale = 2)
    private BigDecimal bodyFatPercentage;

    @Column(precision = 6, scale = 2)
    private BigDecimal muscleMassKg;

    @Column(precision = 6, scale = 2)
    private BigDecimal chestCm;

    @Column(precision = 6, scale = 2)
    private BigDecimal waistCm;

    @Column(precision = 6, scale = 2)
    private BigDecimal armsCm;

    @Column(precision = 5, scale = 2)
    private BigDecimal bmi;

    @Column(length = 50)
    private String bmiCategory;

    @Column(length = 255)
    private String milestoneBadge;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (recordDate == null) {
            recordDate = LocalDate.now();
        }
    }
}