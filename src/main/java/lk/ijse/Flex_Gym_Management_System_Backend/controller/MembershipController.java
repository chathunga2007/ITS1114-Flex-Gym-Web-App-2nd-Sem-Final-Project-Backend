package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MembershipDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MembershipRequestDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.MembershipService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/memberships")
public class MembershipController {
    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping(value = "/saveMembership", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveMembership(@RequestBody MembershipRequestDTO requestDTO) {
        MembershipDTO savedMembershipDTO = membershipService.createMembership(requestDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedMembershipDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateMembership/{membershipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateMembership(@PathVariable Long membershipId, @RequestBody MembershipRequestDTO requestDTO) {
        MembershipDTO updatedMembershipDTO = membershipService.updateMembership(membershipId, requestDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedMembershipDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteMembership/{membershipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteMembership(@PathVariable Long membershipId) {
        String deleteMessage = membershipService.deleteMembership(membershipId);
        return new CommonResponse(OPERATION_SUCCESS, deleteMessage, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllMemberships", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllMemberships() {
        List<MembershipDTO> membershipDTOList = membershipService.getAllActiveMemberships();
        return new CommonResponse(OPERATION_SUCCESS, membershipDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getMembership/{membershipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMembership(@PathVariable Long membershipId) {
        MembershipDTO membershipDTO = membershipService.getMembershipById(membershipId);
        return new CommonResponse(OPERATION_SUCCESS, membershipDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getMembershipsByMember/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMembershipsByMember(@PathVariable Long memberId) {
        List<MembershipDTO> membershipDTOList = membershipService.getMembershipsByMemberId(memberId);
        return new CommonResponse(OPERATION_SUCCESS, membershipDTOList, SUCCESS_MESSAGE);
    }
}