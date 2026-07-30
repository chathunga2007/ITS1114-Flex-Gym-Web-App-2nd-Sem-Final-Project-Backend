package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.PackageDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Package;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PackageStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.PackageRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        try {
            Optional<Package> existingPackage = packageRepository.findByPackageNameAndPackageStatus(
                    packageDTO.getPackageName(), PackageStatus.ACTIVE);

            if (existingPackage.isPresent()) {
                throw new RuntimeException("Package with name '" + packageDTO.getPackageName() + "' already exists!");
            }

            Package Package = new Package();
            Package.setPackageName(packageDTO.getPackageName());
            Package.setPackageDescription(packageDTO.getPackageDescription());
            Package.setPackagePrice(packageDTO.getPackagePrice());
            Package.setDurationMonths(packageDTO.getDurationMonths());
            Package.setPackageStatus(PackageStatus.ACTIVE);

            Package savedPackage = packageRepository.save(Package);
            log.info("Package saved successfully!");

            packageDTO.setPackageId(savedPackage.getPackageId());
            packageDTO.setPackageStatus(savedPackage.getPackageStatus());

            return packageDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PackageDTO updatePackage(PackageDTO packageDTO) {
        log.info("Execute Update Package for ID: {}", packageDTO.getPackageId());
        try {
            Optional<Package> optionalPackage = packageRepository.findById(packageDTO.getPackageId());

            if (optionalPackage.isEmpty() || optionalPackage.get().getPackageStatus() == PackageStatus.DELETED) {
                throw new RuntimeException("Package not found or already deleted!");
            }

            Optional<Package> existingPackage = packageRepository.findByPackageNameAndPackageStatus(
                    packageDTO.getPackageName(), PackageStatus.ACTIVE);

            if (existingPackage.isPresent() && !existingPackage.get().getPackageId().equals(packageDTO.getPackageId())) {
                throw new RuntimeException("Another active package already exists with name: " + packageDTO.getPackageName());
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
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PackageDTO getPackageById(Long id) {
        log.info("Execute Get Package By ID: {}", id);
        try {
            Optional<Package> optionalPackage = packageRepository.findById(id);

            if (optionalPackage.isEmpty() || optionalPackage.get().getPackageStatus() == PackageStatus.DELETED) {
                throw new RuntimeException("Package not found with ID: " + id);
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
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<PackageDTO> getAllActivePackages() {
        log.info("Execute Get All Active Packages");
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String deletePackage(Long id) {
        log.info("Execute Soft Delete Package for ID: {}", id);
        try {
            Optional<Package> optionalPackage = packageRepository.findById(id);

            if (optionalPackage.isEmpty()) {
                throw new RuntimeException("Package not found!");
            }

            Package pkg = optionalPackage.get();

            if (pkg.getPackageStatus() == PackageStatus.DELETED) {
                throw new RuntimeException("Package is already deleted!");
            }

            pkg.setPackageStatus(PackageStatus.DELETED);
            packageRepository.save(pkg);

            log.info("Package soft-deleted successfully!");
            return "Package deleted successfully!";

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}