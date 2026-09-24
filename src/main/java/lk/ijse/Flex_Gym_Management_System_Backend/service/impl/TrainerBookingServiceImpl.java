package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.TrainerBookingDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Trainer;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.TrainerBooking;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.BookingStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.TrainerBookingRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.TrainerRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import lk.ijse.Flex_Gym_Management_System_Backend.service.TrainerBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerBookingServiceImpl implements TrainerBookingService {
    private final TrainerBookingRepository bookingRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public TrainerBookingDTO createBooking(TrainerBookingDTO bookingDTO) {
        log.info("Execute createBooking() for Member ID: {}", bookingDTO.getMemberId());

        if (bookingDTO.getMemberId() == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }
        if (bookingDTO.getTrainerId() == null) {
            throw new CustomException(400, "Trainer ID cannot be null!");
        }
        if (bookingDTO.getSessionDate() == null) {
            throw new CustomException(400, "Session Date is required!");
        }
        if (bookingDTO.getTimeSlot() == null || bookingDTO.getTimeSlot().isBlank()) {
            throw new CustomException(400, "Time slot is required!");
        }

        Member member = memberRepository.findById(bookingDTO.getMemberId())
                .orElseThrow(() -> new CustomException(404, "Member not found with ID: " + bookingDTO.getMemberId()));

        Trainer trainer = trainerRepository.findById(bookingDTO.getTrainerId())
                .orElseThrow(() -> new CustomException(404, "Trainer not found with ID: " + bookingDTO.getTrainerId()));

        TrainerBooking booking = new TrainerBooking();
        booking.setMember(member);
        booking.setTrainer(trainer);
        booking.setSessionDate(bookingDTO.getSessionDate());
        booking.setTimeSlot(bookingDTO.getTimeSlot().trim());
        booking.setFocusArea(bookingDTO.getFocusArea() != null ? bookingDTO.getFocusArea().trim() : "Full Body Fitness");
        booking.setStatus(bookingDTO.getStatus() != null ? bookingDTO.getStatus() : BookingStatus.SCHEDULED);
        booking.setMemberNotes(bookingDTO.getMemberNotes());
        booking.setCreatedAt(LocalDateTime.now());

        TrainerBooking saved = bookingRepository.save(booking);
        log.info("Trainer booking created successfully with ID: {}", saved.getBookingId());

        // Send confirmation email
        try {
            String memberEmail = (member.getUser() != null) ? member.getUser().getEmail() : null;
            if (memberEmail != null && !memberEmail.isBlank()) {
                emailService.sendBookingConfirmationEmail(
                        memberEmail,
                        member.getMemberFullName(),
                        trainer.getTrainerName(),
                        saved.getSessionDate().toString(),
                        saved.getTimeSlot(),
                        saved.getFocusArea()
                );
            }
        } catch (Exception e) {
            log.error("Failed to trigger booking confirmation email: {}", e.getMessage());
        }

        return mapToDTO(saved);
    }

    @Override
    public List<TrainerBookingDTO> getMemberBookings(Long memberId) {
        log.info("Execute getMemberBookings() for Member ID: {}", memberId);
        return bookingRepository.findByMember_MemberIdOrderBySessionDateDesc(memberId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TrainerBookingDTO> getTrainerBookings(Long trainerId) {
        log.info("Execute getTrainerBookings() for Trainer ID: {}", trainerId);
        return bookingRepository.findByTrainer_TrainerIdOrderBySessionDateDesc(trainerId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TrainerBookingDTO> getTodayTrainerBookings(Long trainerId) {
        log.info("Execute getTodayTrainerBookings() for Trainer ID: {}", trainerId);
        return bookingRepository.findByTrainer_TrainerIdAndSessionDate(trainerId, LocalDate.now())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TrainerBookingDTO> getAllBookings() {
        log.info("Execute getAllBookings()");
        return bookingRepository.findAllByOrderBySessionDateDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TrainerBookingDTO updateBookingStatus(Long bookingId, BookingStatus status, String trainerFeedback) {
        log.info("Execute updateBookingStatus() for booking ID: {} -> {}", bookingId, status);
        TrainerBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Booking not found with ID: " + bookingId));

        if (status != null) {
            booking.setStatus(status);
        }
        if (trainerFeedback != null) {
            booking.setTrainerFeedback(trainerFeedback);
        }

        TrainerBooking updated = bookingRepository.save(booking);
        return mapToDTO(updated);
    }

    private TrainerBookingDTO mapToDTO(TrainerBooking b) {
        TrainerBookingDTO dto = new TrainerBookingDTO();
        dto.setBookingId(b.getBookingId());
        if (b.getMember() != null) {
            dto.setMemberId(b.getMember().getMemberId());
            dto.setMemberName(b.getMember().getMemberFullName());
            dto.setMemberPhone(b.getMember().getMemberPhoneNumber());
        }
        if (b.getTrainer() != null) {
            dto.setTrainerId(b.getTrainer().getTrainerId());
            dto.setTrainerName(b.getTrainer().getTrainerName());
            dto.setTrainerSpecialization(b.getTrainer().getSpecialization());
        }
        dto.setSessionDate(b.getSessionDate());
        dto.setTimeSlot(b.getTimeSlot());
        dto.setFocusArea(b.getFocusArea());
        dto.setStatus(b.getStatus());
        dto.setMemberNotes(b.getMemberNotes());
        dto.setTrainerFeedback(b.getTrainerFeedback());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }
}