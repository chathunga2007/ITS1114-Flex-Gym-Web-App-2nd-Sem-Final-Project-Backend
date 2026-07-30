package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO {
    private Long packageId;
    private String packageName;
    private String packageDescription;
    private BigDecimal packagePrice;
    private int durationMonths;
    private PackageStatus packageStatus;
}