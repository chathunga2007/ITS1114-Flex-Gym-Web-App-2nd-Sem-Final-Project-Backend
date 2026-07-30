package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.PackageDTO;
import java.util.List;

public interface PackageService {
    PackageDTO savePackage(PackageDTO packageDTO);
    PackageDTO updatePackage(PackageDTO packageDTO);
    PackageDTO getPackageById(Long id);
    List<PackageDTO> getAllActivePackages();
    String deletePackage(Long id);
}