package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.MemberDTO;
import java.util.List;

public interface MemberService {
    MemberDTO saveMember(MemberDTO memberDTO);
    MemberDTO updateMember(Long memberId, MemberDTO memberDTO);
    MemberDTO getMemberById(Long id);
    List<MemberDTO> getAllActiveMembers();
    String deleteMember(Long id);
}