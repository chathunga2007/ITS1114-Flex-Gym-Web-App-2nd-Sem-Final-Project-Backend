package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FitnessProgressDTO {
    private Long progressId;
    private Long memberId;
    private String memberName;
    private LocalDate recordDate;
    private BigDecimal weightKg;
    private BigDecimal bodyFatPercentage;
    private BigDecimal muscleMassKg;
    private BigDecimal chestCm;
    private BigDecimal waistCm;
    private BigDecimal armsCm;
    private BigDecimal bmi;
    private String bmiCategory;
    private String milestoneBadge;
    private String notes;
    private BigDecimal weightChangeKg;
    private LocalDateTime createdAt;
}