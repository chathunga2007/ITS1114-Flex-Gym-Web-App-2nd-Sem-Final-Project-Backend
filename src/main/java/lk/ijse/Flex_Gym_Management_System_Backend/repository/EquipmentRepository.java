package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.Equipment;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByConditionStatus(EquipmentStatus conditionStatus);
}