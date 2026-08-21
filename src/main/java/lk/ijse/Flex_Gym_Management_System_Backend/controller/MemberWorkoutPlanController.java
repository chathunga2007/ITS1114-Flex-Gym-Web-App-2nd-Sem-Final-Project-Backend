package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MemberWorkoutPlanDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.MemberWorkoutPlanService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/member-workout-plans")
public class MemberWorkoutPlanController {
    private final MemberWorkoutPlanService memberWorkoutPlanService;

    public MemberWorkoutPlanController(MemberWorkoutPlanService memberWorkoutPlanService) {
        this.memberWorkoutPlanService = memberWorkoutPlanService;
    }

    @PostMapping(value = "/assignPlan", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse assignWorkoutPlan(@RequestBody MemberWorkoutPlanDTO dto) {
        MemberWorkoutPlanDTO saved = memberWorkoutPlanService.assignWorkoutPlan(dto);
        return new CommonResponse(OPERATION_SUCCESS, saved, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updatePlan", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateMemberWorkoutPlan(@RequestBody MemberWorkoutPlanDTO dto) {
        MemberWorkoutPlanDTO updated = memberWorkoutPlanService.updateMemberWorkoutPlan(dto);
        return new CommonResponse(OPERATION_SUCCESS, updated, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deletePlan/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteMemberWorkoutPlan(@PathVariable Long id) {
        String response = memberWorkoutPlanService.deleteMemberWorkoutPlan(id);
        return new CommonResponse(OPERATION_SUCCESS, response, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllPlans", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllMemberWorkoutPlans() {
        List<MemberWorkoutPlanDTO> list = memberWorkoutPlanService.getAllMemberWorkoutPlans();
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getPlan/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMemberWorkoutPlan(@PathVariable Long id) {
        MemberWorkoutPlanDTO dto = memberWorkoutPlanService.getMemberWorkoutPlanById(id);
        return new CommonResponse(OPERATION_SUCCESS, dto, SUCCESS_MESSAGE);
    }
}