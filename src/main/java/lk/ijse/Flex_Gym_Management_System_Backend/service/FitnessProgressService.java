package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.FitnessProgressDTO;
import java.util.List;

public interface FitnessProgressService {
    FitnessProgressDTO logProgress(FitnessProgressDTO dto);
    List<FitnessProgressDTO> getMemberProgressHistory(Long memberId);
    FitnessProgressDTO getLatestProgress(Long memberId);
    void deleteProgress(Long progressId);
}