package lk.ijse.Flex_Gym_Management_System_Backend.entity;

import jakarta.persistence.*;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PackageStatus;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "packages")
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;
    private String packageName;
    private String packageDescription;
    private BigDecimal packagePrice;
    private int durationMonths;
    @Enumerated(EnumType.STRING)
    private PackageStatus  packageStatus = PackageStatus.ACTIVE;
}