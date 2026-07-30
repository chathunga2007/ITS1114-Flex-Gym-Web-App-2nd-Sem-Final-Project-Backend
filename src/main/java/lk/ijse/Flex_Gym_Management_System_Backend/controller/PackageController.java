package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.PackageDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.PackageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/packages")
public class PackageController {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping(value = "/savePackage", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse savePackage(@RequestBody PackageDTO packageDTO) {
        PackageDTO savedPackageDTO = packageService.savePackage(packageDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedPackageDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updatePackage", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updatePackage(@RequestBody PackageDTO packageDTO) {
        PackageDTO updatedPackageDTO = packageService.updatePackage(packageDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedPackageDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deletePackage/{packageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deletePackage(@PathVariable Long packageId) {
        String deletePackage = packageService.deletePackage(packageId);
        return new CommonResponse(OPERATION_SUCCESS, deletePackage, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllPackages", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllPackages() {
        List<PackageDTO> packageDTOList = packageService.getAllActivePackages();
        return new CommonResponse(OPERATION_SUCCESS, packageDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getPackage/{packageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPackage(@PathVariable Long packageId) {
        PackageDTO packageDTO = packageService.getPackageById(packageId);
        return new CommonResponse(OPERATION_SUCCESS, packageDTO, SUCCESS_MESSAGE);
    }
}