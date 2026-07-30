package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.MembershipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MembershipDTO {
    private Long membershipId;
    private LocalDate startDate;
    private LocalDate endDate;
    private MembershipStatus membershipStatus;

    private Long memberId;
    private String memberName;

    private Long packageId;
    private String packageName;
}