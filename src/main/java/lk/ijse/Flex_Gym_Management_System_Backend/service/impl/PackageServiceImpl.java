package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.PackageDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Package;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PackageStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.PackageRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.PackageService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PackageServiceImpl implements PackageService {
    private final PackageRepository packageRepository;

    public PackageServiceImpl(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    @Override
    public PackageDTO savePackage(PackageDTO packageDTO) {
        log.info("Execute Save Package!");
        if (packageDTO == null) {
            throw new CustomException(400, "Package data cannot be null!");
        }
        if (packageDTO.getPackageName() == null || packageDTO.getPackageName().trim().isEmpty()) {
            throw new CustomException(400, "Package name cannot be empty!");
        }
        if (packageDTO.getPackagePrice() == null) {
            throw new CustomException(400, "Package price cannot be null!");
        }
        if (packageDTO.getDurationMonths() <= 0) {
            throw new CustomException(400, "Duration months must be greater than 0!");
        }

        Optional<Package> existingPackage = packageRepository.findByPackageNameAndPackageStatus(
                packageDTO.getPackageName(), PackageStatus.ACTIVE);

        if (existingPackage.isPresent()) {
            throw new CustomException(409, "Package with name '" + packageDTO.getPackageName() + "' already exists!");
        }

        Package pkg = new Package();
        pkg.setPackageName(packageDTO.getPackageName());
        pkg.setPackageDescription(packageDTO.getPackageDescription());
        pkg.setPackagePrice(packageDTO.getPackagePrice());
        pkg.setDurationMonths(packageDTO.getDurationMonths());
        pkg.setPackageStatus(PackageStatus.ACTIVE);

        Package savedPackage = packageRepository.save(pkg);
        log.info("Package saved successfully!");

        packageDTO.setPackageId(savedPackage.getPackageId());
        packageDTO.setPackageStatus(savedPackage.getPackageStatus());

        return packageDTO;
    }

    @Override
    public PackageDTO updatePackage(PackageDTO packageDTO) {
        log.info("Execute Update Package");
        if (packageDTO == null) {
            throw new CustomException(400, "Package data cannot be null!");
        }
        if (packageDTO.getPackageId() == null) {
            throw new CustomException(400, "Package ID cannot be null!");
        }
        if (packageDTO.getPackageName() == null || packageDTO.getPackageName().trim().isEmpty()) {
            throw new CustomException(400, "Package name cannot be empty!");
        }

        Optional<Package> optionalPackage = packageRepository.findById(packageDTO.getPackageId());

        if (optionalPackage.isEmpty() || optionalPackage.get().getPackageStatus() == PackageStatus.DELETED) {
            throw new CustomException(404, "Package not found or already deleted!");
        }

        Optional<Package> existingPackage = packageRepository.findByPackageNameAndPackageStatus(
                packageDTO.getPackageName(), PackageStatus.ACTIVE);

        if (existingPackage.isPresent() && !existingPackage.get().getPackageId().equals(packageDTO.getPackageId())) {
            throw new CustomException(409, "Another active package already exists with name: " + packageDTO.getPackageName());
        }

        Package pkg = optionalPackage.get();

        pkg.setPackageName(packageDTO.getPackageName());
        pkg.setPackageDescription(packageDTO.getPackageDescription());
        pkg.setPackagePrice(packageDTO.getPackagePrice());
        pkg.setDurationMonths(packageDTO.getDurationMonths());
        pkg.setPackageStatus(packageDTO.getPackageStatus());

        Package updatedPackage = packageRepository.save(pkg);
        log.info("Package updated successfully!");

        packageDTO.setPackageStatus(updatedPackage.getPackageStatus());
        return packageDTO;
    }

    @Override
    public PackageDTO getPackageById(Long id) {
        log.info("Execute Get Package By ID");
        if (id == null) {
            throw new CustomException(400, "Package ID cannot be null!");
        }

        Optional<Package> optionalPackage = packageRepository.findById(id);

        if (optionalPackage.isEmpty() || optionalPackage.get().getPackageStatus() == PackageStatus.DELETED) {
            throw new CustomException(404, "Package not found with ID: " + id);
        }

        Package pkg = optionalPackage.get();
        return new PackageDTO(
                pkg.getPackageId(),
                pkg.getPackageName(),
                pkg.getPackageDescription(),
                pkg.getPackagePrice(),
                pkg.getDurationMonths(),
                pkg.getPackageStatus()
        );
    }

    @Override
    public List<PackageDTO> getAllActivePackages() {
        log.info("Execute Get All Active Packages");
        List<Package> packageList = packageRepository.findAllByPackageStatus(PackageStatus.ACTIVE);
        List<PackageDTO> dtoList = new ArrayList<>();

        for (Package pkg : packageList) {
            dtoList.add(new PackageDTO(
                    pkg.getPackageId(),
                    pkg.getPackageName(),
                    pkg.getPackageDescription(),
                    pkg.getPackagePrice(),
                    pkg.getDurationMonths(),
                    pkg.getPackageStatus()
            ));
        }
        return dtoList;
    }

    @Override
    public String deletePackage(Long id) {
        log.info("Execute Soft Delete Package for ID");
        if (id == null) {
            throw new CustomException(400, "Package ID cannot be null!");
        }

        Optional<Package> optionalPackage = packageRepository.findById(id);

        if (optionalPackage.isEmpty()) {
            throw new CustomException(404, "Package not found!");
        }

        Package pkg = optionalPackage.get();

        if (pkg.getPackageStatus() == PackageStatus.DELETED) {
            throw new CustomException(400, "Package is already deleted!");
        }

        pkg.setPackageStatus(PackageStatus.DELETED);
        packageRepository.save(pkg);

        log.info("Package deleted successfully!");
        return "Package deleted successfully!";
    }
}