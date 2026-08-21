package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.EquipmentDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Equipment;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.EquipmentStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
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
        log.info("Execute saveEquipment()");
        if (equipmentDTO == null) {
            throw new CustomException(400, "Equipment data cannot be null!");
        }
        if (equipmentDTO.getEquipmentName() == null || equipmentDTO.getEquipmentName().trim().isEmpty()) {
            throw new CustomException(400, "Equipment name cannot be empty!");
        }
        if (equipmentDTO.getQuantity() == null || equipmentDTO.getQuantity() < 0) {
            throw new CustomException(400, "Equipment quantity must be valid!");
        }

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
        log.info("Execute updateEquipment()");
        if (equipmentDTO == null) {
            throw new CustomException(400, "Equipment data cannot be null!");
        }
        if (equipmentDTO.getEquipmentId() == null) {
            throw new CustomException(400, "Equipment ID cannot be null for update!");
        }
        if (equipmentDTO.getEquipmentName() == null || equipmentDTO.getEquipmentName().trim().isEmpty()) {
            throw new CustomException(400, "Equipment name cannot be empty!");
        }

        Optional<Equipment> optionalEquipment = equipmentRepository.findById(equipmentDTO.getEquipmentId());
        if (optionalEquipment.isEmpty()) {
            throw new CustomException(404, "Equipment not found with ID: " + equipmentDTO.getEquipmentId());
        }

        Equipment equipment = optionalEquipment.get();
        if (equipment.getConditionStatus() == EquipmentStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted equipment!");
        }

        equipment.setEquipmentName(equipmentDTO.getEquipmentName());
        equipment.setQuantity(equipmentDTO.getQuantity());
        equipment.setConditionStatus(equipmentDTO.getConditionStatus());
        equipment.setLastMaintenanceDate(equipmentDTO.getLastMaintenanceDate());

        equipmentRepository.save(equipment);
        log.info("Equipment updated successfully!");

        return equipmentDTO;
    }

    @Override
    public EquipmentDTO getEquipmentById(Long id) {
        log.info("Execute getEquipmentById()");
        if (id == null) {
            throw new CustomException(400, "Equipment ID cannot be null!");
        }

        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);
        if (optionalEquipment.isEmpty()) {
            throw new CustomException(404, "Equipment not found with ID: " + id);
        }

        Equipment eq = optionalEquipment.get();
        if (eq.getConditionStatus() == EquipmentStatus.DELETED) {
            throw new CustomException(404, "Equipment not found with ID: " + id);
        }

        return new EquipmentDTO(
                eq.getEquipmentId(),
                eq.getEquipmentName(),
                eq.getQuantity(),
                eq.getConditionStatus(),
                eq.getLastMaintenanceDate()
        );
    }

    @Override
    public List<EquipmentDTO> getAllEquipments() {
        log.info("Execute getAllEquipments()");
        List<Equipment> equipmentList = equipmentRepository.findAll();
        List<EquipmentDTO> dtoList = new ArrayList<>();

        for (Equipment eq : equipmentList) {
            if (eq.getConditionStatus() != EquipmentStatus.DELETED) {
                dtoList.add(new EquipmentDTO(
                        eq.getEquipmentId(),
                        eq.getEquipmentName(),
                        eq.getQuantity(),
                        eq.getConditionStatus(),
                        eq.getLastMaintenanceDate()
                ));
            }
        }
        return dtoList;
    }

    @Override
    public String deleteEquipment(Long id) {
        log.info("Execute deleteEquipment() for ID: " + id);
        if (id == null) {
            throw new CustomException(400, "Equipment ID cannot be null!");
        }

        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);
        if (optionalEquipment.isEmpty()) {
            throw new CustomException(404, "Equipment not found with ID: " + id);
        }

        Equipment equipment = optionalEquipment.get();
        if (equipment.getConditionStatus() == EquipmentStatus.DELETED) {
            throw new CustomException(400, "Equipment is already deleted!");
        }

        equipment.setConditionStatus(EquipmentStatus.DELETED);
        equipmentRepository.save(equipment);

        log.info("Equipment marked as DELETED successfully!");
        return "Equipment deleted successfully!";
    }
}