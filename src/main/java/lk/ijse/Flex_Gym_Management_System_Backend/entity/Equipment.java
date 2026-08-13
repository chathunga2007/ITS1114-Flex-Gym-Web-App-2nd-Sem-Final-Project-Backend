package lk.ijse.Flex_Gym_Management_System_Backend.entity;

import jakarta.persistence.*;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipments")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long equipmentId;
    private String equipmentName;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private EquipmentStatus conditionStatus;
    private LocalDate lastMaintenanceDate;
}