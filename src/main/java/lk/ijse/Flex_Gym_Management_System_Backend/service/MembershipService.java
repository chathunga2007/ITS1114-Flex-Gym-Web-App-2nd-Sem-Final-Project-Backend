package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.MembershipDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MembershipRequestDTO;
import java.util.List;

public interface MembershipService {
    MembershipDTO createMembership(MembershipRequestDTO requestDTO);
    MembershipDTO updateMembership(Long membershipId, MembershipRequestDTO requestDTO);
    MembershipDTO getMembershipById(Long id);
    List<MembershipDTO> getMembershipsByMemberId(Long memberId);
    List<MembershipDTO> getAllActiveMemberships();
    String deleteMembership(Long id);
}