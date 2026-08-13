package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.TrainerDTO;
import java.util.List;

public interface TrainerService {
    TrainerDTO saveTrainer(TrainerDTO trainerDTO);
    TrainerDTO updateTrainer(TrainerDTO trainerDTO);
    TrainerDTO getTrainerById(Long id);
    List<TrainerDTO> getAllTrainers();
    String deleteTrainer(Long id);
}