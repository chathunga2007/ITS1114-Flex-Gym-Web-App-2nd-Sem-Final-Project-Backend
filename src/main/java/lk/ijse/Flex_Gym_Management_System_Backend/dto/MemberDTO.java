package lk.ijse.Flex_Gym_Management_System_Backend.dto;

import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long memberId;
    private String memberFullName;
    private String memberPhoneNumber;
    private String email;
    private String password;
    private String age;
    private String gender;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private MemberStatus memberStatus;
}