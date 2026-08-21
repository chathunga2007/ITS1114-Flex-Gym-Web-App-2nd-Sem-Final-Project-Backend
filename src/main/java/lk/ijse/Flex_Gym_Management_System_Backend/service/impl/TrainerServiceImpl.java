package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.TrainerDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Trainer;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.User;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.TrainerStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.UserRole;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.TrainerRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.UserRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import lk.ijse.Flex_Gym_Management_System_Backend.service.TrainerService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public TrainerServiceImpl(TrainerRepository trainerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public TrainerDTO saveTrainer(TrainerDTO trainerDTO) {
        log.info("Execute saveTrainer()");
        if (trainerDTO == null) {
            throw new CustomException(400, "Trainer data cannot be null!");
        }
        if (trainerDTO.getTrainerName() == null || trainerDTO.getTrainerName().trim().isEmpty()) {
            throw new CustomException(400, "Trainer name cannot be empty!");
        }
        if (trainerDTO.getEmail() == null || trainerDTO.getEmail().trim().isEmpty()) {
            throw new CustomException(400, "Trainer email cannot be empty!");
        }

        if (userRepository.existsByEmail(trainerDTO.getEmail())) {
            throw new CustomException(409, "This email is already registered in the system!");
        }

        String rawPassword = (trainerDTO.getPassword() != null && !trainerDTO.getPassword().isBlank())
                ? trainerDTO.getPassword()
                : "Trainer@" + (int) (Math.random() * 9000 + 1000);

        User newUser = new User();
        newUser.setEmail(trainerDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setUserRole(UserRole.ROLE_TRAINER);
        User savedUser = userRepository.save(newUser);

        if (trainerDTO.getStatus() == null) {
            trainerDTO.setStatus(TrainerStatus.ACTIVE);
        }

        Trainer trainer = new Trainer();
        trainer.setTrainerName(trainerDTO.getTrainerName());
        trainer.setSpecialization(trainerDTO.getSpecialization());
        trainer.setPhoneNumber(trainerDTO.getPhoneNumber());
        trainer.setEmail(trainerDTO.getEmail());
        trainer.setStatus(trainerDTO.getStatus());
        trainer.setUser(savedUser);

        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Trainer profile & User account created successfully!");

        emailService.sendAccountCredentialsEmail(trainerDTO.getEmail(), trainerDTO.getTrainerName(), rawPassword);

        trainerDTO.setTrainerId(savedTrainer.getTrainerId());
        trainerDTO.setPassword(null);
        return trainerDTO;
    }

    @Override
    public TrainerDTO updateTrainer(TrainerDTO trainerDTO) {
        log.info("Execute updateTrainer()");
        if (trainerDTO == null || trainerDTO.getTrainerId() == null) {
            throw new CustomException(400, "Trainer ID cannot be null for update!");
        }

        Optional<Trainer> optionalTrainer = trainerRepository.findById(trainerDTO.getTrainerId());
        if (optionalTrainer.isEmpty()) {
            throw new CustomException(404, "Trainer not found with ID: " + trainerDTO.getTrainerId());
        }

        Trainer trainer = optionalTrainer.get();
        if (trainer.getStatus() == TrainerStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted trainer!");
        }

        trainer.setTrainerName(trainerDTO.getTrainerName());
        trainer.setSpecialization(trainerDTO.getSpecialization());
        trainer.setPhoneNumber(trainerDTO.getPhoneNumber());
        trainer.setEmail(trainerDTO.getEmail());
        if (trainerDTO.getStatus() != null) {
            trainer.setStatus(trainerDTO.getStatus());
        }

        trainerRepository.save(trainer);
        log.info("Trainer updated successfully!");

        return trainerDTO;
    }

    @Override
    public TrainerDTO getTrainerById(Long id) {
        log.info("Execute getTrainerById()");
        if (id == null) {
            throw new CustomException(400, "Trainer ID cannot be null!");
        }

        Optional<Trainer> optionalTrainer = trainerRepository.findById(id);
        if (optionalTrainer.isEmpty()) {
            throw new CustomException(404, "Trainer not found with ID: " + id);
        }

        Trainer tr = optionalTrainer.get();
        if (tr.getStatus() == TrainerStatus.DELETED) {
            throw new CustomException(404, "Trainer not found with ID: " + id);
        }

        TrainerDTO dto = new TrainerDTO();
        dto.setTrainerId(tr.getTrainerId());
        dto.setTrainerName(tr.getTrainerName());
        dto.setSpecialization(tr.getSpecialization());
        dto.setPhoneNumber(tr.getPhoneNumber());
        dto.setEmail(tr.getEmail());
        dto.setStatus(tr.getStatus());
        dto.setPassword(null);

        return dto;
    }

    @Override
    public List<TrainerDTO> getAllTrainers() {
        log.info("Execute getAllTrainers()");
        List<Trainer> trainerList = trainerRepository.findAllByStatus(TrainerStatus.ACTIVE);
        List<TrainerDTO> dtoList = new ArrayList<>();

        for (Trainer tr : trainerList) {
            TrainerDTO dto = new TrainerDTO();
            dto.setTrainerId(tr.getTrainerId());
            dto.setTrainerName(tr.getTrainerName());
            dto.setSpecialization(tr.getSpecialization());
            dto.setPhoneNumber(tr.getPhoneNumber());
            dto.setEmail(tr.getEmail());
            dto.setStatus(tr.getStatus());
            dto.setPassword(null);

            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public String deleteTrainer(Long id) {
        log.info("Execute deleteTrainer()");
        if (id == null) {
            throw new CustomException(400, "Trainer ID cannot be null!");
        }

        Optional<Trainer> optionalTrainer = trainerRepository.findById(id);
        if (optionalTrainer.isEmpty()) {
            throw new CustomException(404, "Trainer not found with ID: " + id);
        }

        Trainer trainer = optionalTrainer.get();
        if (trainer.getStatus() == TrainerStatus.DELETED) {
            throw new CustomException(400, "Trainer is already deleted!");
        }

        trainer.setStatus(TrainerStatus.DELETED);
        trainerRepository.save(trainer);

        log.info("Trainer marked as DELETED successfully!");
        return "Trainer deleted successfully!";
    }
}