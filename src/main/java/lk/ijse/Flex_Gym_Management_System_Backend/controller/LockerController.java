package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.LockerDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.LockerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/lockers")
public class LockerController {
    private final LockerService lockerService;

    public LockerController(LockerService lockerService) {
        this.lockerService = lockerService;
    }

    @PostMapping(value = "/saveLocker", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveLocker(@RequestBody LockerDTO lockerDTO) {
        LockerDTO savedLocker = lockerService.saveLocker(lockerDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedLocker, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateLocker", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateLocker(@RequestBody LockerDTO lockerDTO) {
        LockerDTO updatedLocker = lockerService.updateLocker(lockerDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedLocker, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteLocker/{lockerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteLocker(@PathVariable Long lockerId) {
        String deleteMessage = lockerService.deleteLocker(lockerId);
        return new CommonResponse(OPERATION_SUCCESS, deleteMessage, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllLockers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllLockers() {
        List<LockerDTO> lockerList = lockerService.getAllLockers();
        return new CommonResponse(OPERATION_SUCCESS, lockerList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getLocker/{lockerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getLocker(@PathVariable Long lockerId) {
        LockerDTO lockerDTO = lockerService.getLockerById(lockerId);
        return new CommonResponse(OPERATION_SUCCESS, lockerDTO, SUCCESS_MESSAGE);
    }
}