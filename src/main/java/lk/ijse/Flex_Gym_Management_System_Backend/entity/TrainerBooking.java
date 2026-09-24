package lk.ijse.Flex_Gym_Management_System_Backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainer_bookings")
public class TrainerBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(nullable = false)
    private LocalDate sessionDate;

    @Column(nullable = false, length = 50)
    private String timeSlot;

    @Column(length = 100)
    private String focusArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", length = 30)
    private BookingStatus status = BookingStatus.SCHEDULED;

    @Column(length = 500)
    private String memberNotes;

    @Column(length = 500)
    private String trainerFeedback;

    private LocalDateTime createdAt = LocalDateTime.now();
}