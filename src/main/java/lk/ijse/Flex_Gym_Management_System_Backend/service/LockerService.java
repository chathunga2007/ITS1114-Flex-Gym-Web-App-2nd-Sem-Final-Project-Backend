package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.LockerDTO;
import java.util.List;

public interface LockerService {
    LockerDTO saveLocker(LockerDTO lockerDTO);
    LockerDTO updateLocker(LockerDTO lockerDTO);
    LockerDTO getLockerById(Long id);
    List<LockerDTO> getAllLockers();
    String deleteLocker(Long id);
}