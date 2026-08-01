package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MemberDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping(value = "/saveMember", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO savedMemberDTO = memberService.saveMember(memberDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedMemberDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateMember/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateMember(@PathVariable Long memberId, @RequestBody MemberDTO memberDTO) {
        MemberDTO updatedMemberDTO = memberService.updateMember(memberId, memberDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedMemberDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteMember/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteMember(@PathVariable Long memberId) {
        String deleteMessage = memberService.deleteMember(memberId);
        return new CommonResponse(OPERATION_SUCCESS, deleteMessage, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllMembers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllMembers() {
        List<MemberDTO> memberDTOList = memberService.getAllActiveMembers();
        return new CommonResponse(OPERATION_SUCCESS, memberDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getMember/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMember(@PathVariable Long memberId) {
        MemberDTO memberDTO = memberService.getMemberById(memberId);
        return new CommonResponse(OPERATION_SUCCESS, memberDTO, SUCCESS_MESSAGE);
    }
}