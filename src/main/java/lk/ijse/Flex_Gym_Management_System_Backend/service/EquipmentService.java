package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.EquipmentDTO;
import java.util.List;

public interface EquipmentService {
    EquipmentDTO saveEquipment(EquipmentDTO equipmentDTO);
    EquipmentDTO updateEquipment(EquipmentDTO equipmentDTO);
    EquipmentDTO getEquipmentById(Long id);
    List<EquipmentDTO> getAllEquipments();
    String deleteEquipment(Long id);
}