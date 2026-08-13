package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.TrainerDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.TrainerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping(value = "/saveTrainer", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveTrainer(@RequestBody TrainerDTO trainerDTO) {
        TrainerDTO savedTrainer = trainerService.saveTrainer(trainerDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedTrainer, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateTrainer", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateTrainer(@RequestBody TrainerDTO trainerDTO) {
        TrainerDTO updatedTrainer = trainerService.updateTrainer(trainerDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedTrainer, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteTrainer/{trainerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteTrainer(@PathVariable Long trainerId) {
        String deleteMessage = trainerService.deleteTrainer(trainerId);
        return new CommonResponse(OPERATION_SUCCESS, deleteMessage, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllTrainers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllTrainers() {
        List<TrainerDTO> trainerList = trainerService.getAllTrainers();
        return new CommonResponse(OPERATION_SUCCESS, trainerList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getTrainer/{trainerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getTrainer(@PathVariable Long trainerId) {
        TrainerDTO trainerDTO = trainerService.getTrainerById(trainerId);
        return new CommonResponse(OPERATION_SUCCESS, trainerDTO, SUCCESS_MESSAGE);
    }
}