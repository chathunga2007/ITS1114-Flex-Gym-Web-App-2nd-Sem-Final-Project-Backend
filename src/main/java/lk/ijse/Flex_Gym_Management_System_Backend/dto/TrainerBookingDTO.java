package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerBookingDTO {
    private Long bookingId;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private Long trainerId;
    private String trainerName;
    private String trainerSpecialization;
    private LocalDate sessionDate;
    private String timeSlot;
    private String focusArea;
    private BookingStatus status;
    private String memberNotes;
    private String trainerFeedback;
    private LocalDateTime createdAt;
}