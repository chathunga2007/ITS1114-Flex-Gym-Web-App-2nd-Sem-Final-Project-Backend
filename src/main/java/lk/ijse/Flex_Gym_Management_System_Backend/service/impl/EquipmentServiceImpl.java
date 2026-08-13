package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.EquipmentDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Equipment;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.EquipmentStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.EquipmentRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EquipmentService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public EquipmentDTO saveEquipment(EquipmentDTO equipmentDTO) {
        log.info("Execute Save Equipment!");

        if (equipmentDTO.getConditionStatus() == null) {
            equipmentDTO.setConditionStatus(EquipmentStatus.WORKING); // Default status
        }

        Equipment equipment = new Equipment();
        equipment.setEquipmentName(equipmentDTO.getEquipmentName());
        equipment.setQuantity(equipmentDTO.getQuantity());
        equipment.setConditionStatus(equipmentDTO.getConditionStatus());
        equipment.setLastMaintenanceDate(equipmentDTO.getLastMaintenanceDate());

        Equipment savedEquipment = equipmentRepository.save(equipment);
        log.info("Equipment saved successfully!");

        equipmentDTO.setEquipmentId(savedEquipment.getEquipmentId());
        return equipmentDTO;
    }

    @Override
    public EquipmentDTO updateEquipment(EquipmentDTO equipmentDTO) {
        log.info("Execute Update Equipment");

        Optional<Equipment> optionalEquipment = equipmentRepository.findById(equipmentDTO.getEquipmentId());

        if (optionalEquipment.isPresent()) {
            Equipment equipment = optionalEquipment.get();
            equipment.setEquipmentName(equipmentDTO.getEquipmentName());
            equipment.setQuantity(equipmentDTO.getQuantity());
            equipment.setConditionStatus(equipmentDTO.getConditionStatus());
            equipment.setLastMaintenanceDate(equipmentDTO.getLastMaintenanceDate());

            equipmentRepository.save(equipment);
            log.info("Equipment updated successfully!");
        }

        return equipmentDTO;
    }

    @Override
    public EquipmentDTO getEquipmentById(Long id) {
        log.info("Execute Get Equipment By ID");

        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);

        if (optionalEquipment.isPresent()) {
            Equipment eq = optionalEquipment.get();
            return new EquipmentDTO(
                    eq.getEquipmentId(),
                    eq.getEquipmentName(),
                    eq.getQuantity(),
                    eq.getConditionStatus(),
                    eq.getLastMaintenanceDate()
            );
        }
        return null;
    }

    @Override
    public List<EquipmentDTO> getAllEquipments() {
        log.info("Execute Get All Equipments");
        List<Equipment> equipmentList = equipmentRepository.findAll();
        List<EquipmentDTO> dtoList = new ArrayList<>();

        for (Equipment eq : equipmentList) {
            dtoList.add(new EquipmentDTO(
                    eq.getEquipmentId(),
                    eq.getEquipmentName(),
                    eq.getQuantity(),
                    eq.getConditionStatus(),
                    eq.getLastMaintenanceDate()
            ));
        }
        return dtoList;
    }

    @Override
    public String deleteEquipment(Long id) {
        log.info("Execute Soft Delete Equipment for ID: " + id);

        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);

        if (optionalEquipment.isPresent()) {
            Equipment equipment = optionalEquipment.get();

            equipment.setConditionStatus(EquipmentStatus.DELETED);
            equipmentRepository.save(equipment);

            log.info("Equipment marked as DELETED successfully!");
            return "Equipment deleted successfully!";
        }
        return "Equipment not found!";
    }
}