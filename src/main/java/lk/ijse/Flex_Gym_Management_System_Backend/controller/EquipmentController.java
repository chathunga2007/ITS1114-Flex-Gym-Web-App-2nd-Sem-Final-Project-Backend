package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.EquipmentDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EquipmentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/equipments")
public class EquipmentController {
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping(value = "/saveEquipment", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        EquipmentDTO savedEquipment = equipmentService.saveEquipment(equipmentDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedEquipment, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateEquipment", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        EquipmentDTO updatedEquipment = equipmentService.updateEquipment(equipmentDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedEquipment, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteEquipment/{equipmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteEquipment(@PathVariable Long equipmentId) {
        String deleteMessage = equipmentService.deleteEquipment(equipmentId);
        return new CommonResponse(OPERATION_SUCCESS, deleteMessage, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllEquipments", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllEquipments() {
        List<EquipmentDTO> equipmentList = equipmentService.getAllEquipments();
        return new CommonResponse(OPERATION_SUCCESS, equipmentList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getEquipment/{equipmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getEquipment(@PathVariable Long equipmentId) {
        EquipmentDTO equipmentDTO = equipmentService.getEquipmentById(equipmentId);
        return new CommonResponse(OPERATION_SUCCESS, equipmentDTO, SUCCESS_MESSAGE);
    }
}