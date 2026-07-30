package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRequestDTO {
    private Long memberId;
    private Long packageId;
}