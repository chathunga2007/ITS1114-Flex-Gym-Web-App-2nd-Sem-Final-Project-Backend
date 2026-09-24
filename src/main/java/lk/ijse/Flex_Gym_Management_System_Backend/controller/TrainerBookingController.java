package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.TrainerBookingDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.BookingStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.service.TrainerBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class TrainerBookingController {
    private final TrainerBookingService bookingService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse createBooking(@RequestBody TrainerBookingDTO bookingDTO) {
        TrainerBookingDTO created = bookingService.createBooking(bookingDTO);
        return new CommonResponse(OPERATION_SUCCESS, created, "Personal training session booked successfully!");
    }

    @GetMapping(value = "/member/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMemberBookings(@PathVariable Long memberId) {
        List<TrainerBookingDTO> list = bookingService.getMemberBookings(memberId);
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/trainer/{trainerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getTrainerBookings(@PathVariable Long trainerId) {
        List<TrainerBookingDTO> list = bookingService.getTrainerBookings(trainerId);
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/trainer/{trainerId}/today", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getTodayTrainerBookings(@PathVariable Long trainerId) {
        List<TrainerBookingDTO> list = bookingService.getTodayTrainerBookings(trainerId);
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllBookings() {
        List<TrainerBookingDTO> list = bookingService.getAllBookings();
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/update-status/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus status,
            @RequestParam(required = false) String feedback) {
        TrainerBookingDTO updated = bookingService.updateBookingStatus(bookingId, status, feedback);
        return new CommonResponse(OPERATION_SUCCESS, updated, "Booking status updated successfully!");
    }
}