package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.WorkoutPlanDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.WorkoutPlanService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/workout-plans")
public class WorkoutPlanController {
    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @PostMapping(value = "/saveWorkoutPlan", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveWorkoutPlan(@RequestBody WorkoutPlanDTO workoutPlanDTO) {
        WorkoutPlanDTO savedPlanDTO = workoutPlanService.saveWorkoutPlan(workoutPlanDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedPlanDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateWorkoutPlan", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateWorkoutPlan(@RequestBody WorkoutPlanDTO workoutPlanDTO) {
        WorkoutPlanDTO updatedPlanDTO = workoutPlanService.updateWorkoutPlan(workoutPlanDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedPlanDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteWorkoutPlan/{planId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteWorkoutPlan(@PathVariable Long planId) {
        String deleteResponse = workoutPlanService.deleteWorkoutPlan(planId);
        return new CommonResponse(OPERATION_SUCCESS, deleteResponse, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllWorkoutPlans", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllWorkoutPlans() {
        List<WorkoutPlanDTO> planDTOList = workoutPlanService.getAllWorkoutPlans();
        return new CommonResponse(OPERATION_SUCCESS, planDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getWorkoutPlan/{planId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getWorkoutPlan(@PathVariable Long planId) {
        WorkoutPlanDTO workoutPlanDTO = workoutPlanService.getWorkoutPlanById(planId);
        return new CommonResponse(OPERATION_SUCCESS, workoutPlanDTO, SUCCESS_MESSAGE);
    }
}