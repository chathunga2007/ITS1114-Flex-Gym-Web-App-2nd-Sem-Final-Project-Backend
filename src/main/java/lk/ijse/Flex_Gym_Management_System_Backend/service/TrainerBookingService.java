package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.TrainerBookingDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.BookingStatus;
import java.util.List;

public interface TrainerBookingService {
    TrainerBookingDTO createBooking(TrainerBookingDTO bookingDTO);
    List<TrainerBookingDTO> getMemberBookings(Long memberId);
    List<TrainerBookingDTO> getTrainerBookings(Long trainerId);
    List<TrainerBookingDTO> getTodayTrainerBookings(Long trainerId);
    List<TrainerBookingDTO> getAllBookings();
    TrainerBookingDTO updateBookingStatus(Long bookingId, BookingStatus status, String trainerFeedback);
}