package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.TrainerDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Trainer;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.TrainerStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.TrainerRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.TrainerService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;

    public TrainerServiceImpl(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public TrainerDTO saveTrainer(TrainerDTO trainerDTO) {
        log.info("Execute Save Trainer!");

        if (trainerDTO.getStatus() == null) {
            trainerDTO.setStatus(TrainerStatus.ACTIVE);
        }

        Trainer trainer = new Trainer();
        trainer.setTrainerName(trainerDTO.getTrainerName());
        trainer.setSpecialization(trainerDTO.getSpecialization());
        trainer.setPhoneNumber(trainerDTO.getPhoneNumber());
        trainer.setEmail(trainerDTO.getEmail());
        trainer.setStatus(trainerDTO.getStatus());

        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Trainer saved successfully!");

        trainerDTO.setTrainerId(savedTrainer.getTrainerId());
        return trainerDTO;
    }

    @Override
    public TrainerDTO updateTrainer(TrainerDTO trainerDTO) {
        log.info("Execute Update Trainer");

        Optional<Trainer> optionalTrainer = trainerRepository.findById(trainerDTO.getTrainerId());

        if (optionalTrainer.isPresent()) {
            Trainer trainer = optionalTrainer.get();
            trainer.setTrainerName(trainerDTO.getTrainerName());
            trainer.setSpecialization(trainerDTO.getSpecialization());
            trainer.setPhoneNumber(trainerDTO.getPhoneNumber());
            trainer.setEmail(trainerDTO.getEmail());
            if (trainerDTO.getStatus() != null) {
                trainer.setStatus(trainerDTO.getStatus());
            }

            trainerRepository.save(trainer);
            log.info("Trainer updated successfully!");
        }

        return trainerDTO;
    }

    @Override
    public TrainerDTO getTrainerById(Long id) {
        log.info("Execute Get Trainer By ID");

        Optional<Trainer> optionalTrainer = trainerRepository.findById(id);

        if (optionalTrainer.isEmpty()) {
            System.out.println("Trainer not found with ID: " + id);
            return null;
        }

        Trainer tr = optionalTrainer.get();

        if (tr.getStatus() == TrainerStatus.DELETED) {
            System.out.println("Trainer not found with ID: " + id);
            return null;
        }

        return new TrainerDTO(
                tr.getTrainerId(),
                tr.getTrainerName(),
                tr.getSpecialization(),
                tr.getPhoneNumber(),
                tr.getEmail(),
                tr.getStatus()
        );
    }

    @Override
    public List<TrainerDTO> getAllTrainers() {
        log.info("Execute Get All Active Trainers");
        List<Trainer> trainerList = trainerRepository.findAllByStatus(TrainerStatus.ACTIVE);
        List<TrainerDTO> dtoList = new ArrayList<>();

        for (Trainer tr : trainerList) {
            dtoList.add(new TrainerDTO(
                    tr.getTrainerId(),
                    tr.getTrainerName(),
                    tr.getSpecialization(),
                    tr.getPhoneNumber(),
                    tr.getEmail(),
                    tr.getStatus()
            ));
        }
        return dtoList;
    }

    @Override
    public String deleteTrainer(Long id) {
        log.info("Execute Soft Delete Trainer for ID");

        Optional<Trainer> optionalTrainer = trainerRepository.findById(id);

        if (optionalTrainer.isPresent()) {
            Trainer trainer = optionalTrainer.get();
            trainer.setStatus(TrainerStatus.DELETED);
            trainerRepository.save(trainer);
            log.info("Trainer marked as DELETED successfully!");
            return "Trainer deleted successfully!";
        }
        return "Trainer not found!";
    }
}