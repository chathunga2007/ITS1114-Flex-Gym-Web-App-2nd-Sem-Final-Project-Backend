package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentDTO {
    private Long equipmentId;
    private String equipmentName;
    private Integer quantity;
    private EquipmentStatus conditionStatus;
    private LocalDate lastMaintenanceDate;
}