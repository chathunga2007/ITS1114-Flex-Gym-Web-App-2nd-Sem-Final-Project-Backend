package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.FitnessProgressDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.FitnessProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class FitnessProgressController {
    private final FitnessProgressService progressService;

    @PostMapping(value = "/log", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse logProgress(@RequestBody FitnessProgressDTO dto) {
        FitnessProgressDTO saved = progressService.logProgress(dto);
        return new CommonResponse(OPERATION_SUCCESS, saved, "Fitness check-in and body stats recorded successfully!");
    }

    @GetMapping(value = "/member/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMemberProgressHistory(@PathVariable Long memberId) {
        List<FitnessProgressDTO> list = progressService.getMemberProgressHistory(memberId);
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/member/{memberId}/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getLatestProgress(@PathVariable Long memberId) {
        FitnessProgressDTO latest = progressService.getLatestProgress(memberId);
        return new CommonResponse(OPERATION_SUCCESS, latest, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/{progressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteProgress(@PathVariable Long progressId) {
        progressService.deleteProgress(progressId);
        return new CommonResponse(OPERATION_SUCCESS, null, "Progress log entry deleted successfully.");
    }
}