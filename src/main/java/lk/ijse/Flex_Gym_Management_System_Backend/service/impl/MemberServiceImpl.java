package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MemberDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.User;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.MemberStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.UserRole;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.UserStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.UserRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import lk.ijse.Flex_Gym_Management_System_Backend.service.MemberService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public MemberServiceImpl(MemberRepository memberRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public MemberDTO saveMember(MemberDTO memberDTO) {
        log.info("Execute saveMember()");
        if (memberDTO == null) {
            throw new CustomException(400, "Member data cannot be null!");
        }
        if (memberDTO.getMemberFullName() == null || memberDTO.getMemberFullName().trim().isEmpty()) {
            throw new CustomException(400, "Member full name cannot be empty!");
        }
        if (memberDTO.getMemberPhoneNumber() == null || memberDTO.getMemberPhoneNumber().trim().isEmpty()) {
            throw new CustomException(400, "Member phone number cannot be empty!");
        }
        if (memberDTO.getEmail() == null || memberDTO.getEmail().trim().isEmpty()) {
            throw new CustomException(400, "Member email cannot be empty!");
        }

        if (userRepository.existsByEmail(memberDTO.getEmail())) {
            throw new CustomException(409, "This email is already registered in the system!");
        }

        String rawPassword = (memberDTO.getPassword() != null && !memberDTO.getPassword().isBlank())
                ? memberDTO.getPassword()
                : "FlexGym@" + (int) (Math.random() * 9000 + 1000);

        User newUser = new User();
        newUser.setEmail(memberDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setUserRole(UserRole.ROLE_MEMBER);

        User savedUser = userRepository.save(newUser);

        Member member = new Member();
        member.setMemberFullName(memberDTO.getMemberFullName());
        member.setMemberPhoneNumber(memberDTO.getMemberPhoneNumber());
        member.setAge(memberDTO.getAge());
        member.setGender(memberDTO.getGender());
        member.setHeightCm(memberDTO.getHeightCm());
        member.setWeightKg(memberDTO.getWeightKg());
        member.setMemberStatus(MemberStatus.ACTIVE);
        member.setUser(savedUser);

        Member savedMember = memberRepository.save(member);
        log.info("Member profile & User account created successfully!");

        emailService.sendAccountCredentialsEmail(memberDTO.getEmail(), memberDTO.getMemberFullName(), rawPassword);

        MemberDTO responseDTO = new MemberDTO();
        responseDTO.setMemberId(savedMember.getMemberId());
        responseDTO.setMemberFullName(savedMember.getMemberFullName());
        responseDTO.setMemberPhoneNumber(savedMember.getMemberPhoneNumber());

        if (savedMember.getUser() != null) {
            responseDTO.setEmail(savedMember.getUser().getEmail());
        }

        responseDTO.setAge(savedMember.getAge());
        responseDTO.setGender(savedMember.getGender());
        responseDTO.setHeightCm(savedMember.getHeightCm());
        responseDTO.setWeightKg(savedMember.getWeightKg());
        responseDTO.setMemberStatus(savedMember.getMemberStatus());

        return responseDTO;
    }

    @Override
    public MemberDTO updateMember(Long memberId, MemberDTO memberDTO) {
        log.info("Execute updateMember()");
        if (memberId == null) {
            throw new CustomException(400, "Member ID cannot be null for update!");
        }
        if (memberDTO == null) {
            throw new CustomException(400, "Member data cannot be null!");
        }
        if (memberDTO.getMemberFullName() == null || memberDTO.getMemberFullName().trim().isEmpty()) {
            throw new CustomException(400, "Member full name cannot be empty!");
        }
        if (memberDTO.getMemberPhoneNumber() == null || memberDTO.getMemberPhoneNumber().trim().isEmpty()) {
            throw new CustomException(400, "Member phone number cannot be empty!");
        }

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + memberId);
        }

        Member member = optionalMember.get();

        if (member.getMemberStatus() == MemberStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted member!");
        }

        member.setMemberFullName(memberDTO.getMemberFullName());
        member.setMemberPhoneNumber(memberDTO.getMemberPhoneNumber());
        member.setAge(memberDTO.getAge());
        member.setGender(memberDTO.getGender());
        member.setHeightCm(memberDTO.getHeightCm());
        member.setWeightKg(memberDTO.getWeightKg());

        if (memberDTO.getMemberStatus() != null) {
            member.setMemberStatus(memberDTO.getMemberStatus());
        }

        if (member.getUser() != null) {
            if (memberDTO.getEmail() != null && !memberDTO.getEmail().trim().isEmpty()) {
                member.getUser().setEmail(memberDTO.getEmail().trim());
            }
            if (memberDTO.getMemberStatus() != null) {
                try {
                    member.getUser().setStatus(UserStatus.valueOf(memberDTO.getMemberStatus().name()));
                } catch (Exception ignored) {
                }
            }
        }

        Member updatedMember = memberRepository.save(member);
        log.info("Member updated successfully!");

        MemberDTO responseDTO = new MemberDTO();
        responseDTO.setMemberId(updatedMember.getMemberId());
        responseDTO.setMemberFullName(updatedMember.getMemberFullName());
        responseDTO.setMemberPhoneNumber(updatedMember.getMemberPhoneNumber());

        if (updatedMember.getUser() != null) {
            responseDTO.setEmail(updatedMember.getUser().getEmail());
        }

        responseDTO.setAge(updatedMember.getAge());
        responseDTO.setGender(updatedMember.getGender());
        responseDTO.setHeightCm(updatedMember.getHeightCm());
        responseDTO.setWeightKg(updatedMember.getWeightKg());
        responseDTO.setMemberStatus(updatedMember.getMemberStatus());

        return responseDTO;
    }

    @Override
    public MemberDTO getMemberById(Long id) {
        log.info("Execute getMemberById()");
        if (id == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }

        Optional<Member> optionalMember = memberRepository.findById(id);

        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + id);
        }

        Member member = optionalMember.get();

        if (member.getMemberStatus() == MemberStatus.DELETED) {
            throw new CustomException(404, "Member not found with ID: " + id);
        }

        MemberDTO responseDTO = new MemberDTO();
        responseDTO.setMemberId(member.getMemberId());
        responseDTO.setMemberFullName(member.getMemberFullName());
        responseDTO.setMemberPhoneNumber(member.getMemberPhoneNumber());

        if (member.getUser() != null) {
            responseDTO.setEmail(member.getUser().getEmail());
        }

        responseDTO.setAge(member.getAge());
        responseDTO.setGender(member.getGender());
        responseDTO.setHeightCm(member.getHeightCm());
        responseDTO.setWeightKg(member.getWeightKg());
        responseDTO.setMemberStatus(member.getMemberStatus());

        return responseDTO;
    }

    @Override
    public List<MemberDTO> getAllActiveMembers() {
        log.info("Execute getAllActiveMembers()");
        List<Member> memberList = memberRepository.findAllByMemberStatusNot(MemberStatus.DELETED);
        List<MemberDTO> dtoList = new ArrayList<>();

        for (Member member : memberList) {
            MemberDTO responseDTO = new MemberDTO();
            responseDTO.setMemberId(member.getMemberId());
            responseDTO.setMemberFullName(member.getMemberFullName());
            responseDTO.setMemberPhoneNumber(member.getMemberPhoneNumber());

            if (member.getUser() != null) {
                responseDTO.setEmail(member.getUser().getEmail());
            }

            responseDTO.setAge(member.getAge());
            responseDTO.setGender(member.getGender());
            responseDTO.setHeightCm(member.getHeightCm());
            responseDTO.setWeightKg(member.getWeightKg());
            responseDTO.setMemberStatus(member.getMemberStatus());
            dtoList.add(responseDTO);
        }
        return dtoList;
    }

    @Override
    public String deleteMember(Long id) {
        log.info("Execute deleteMember()");
        if (id == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }

        Optional<Member> optionalMember = memberRepository.findById(id);

        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + id);
        }

        Member member = optionalMember.get();

        if (member.getMemberStatus() == MemberStatus.DELETED) {
            throw new CustomException(400, "Member is already deleted!");
        }

        member.setMemberStatus(MemberStatus.DELETED);
        memberRepository.save(member);

        log.info("Member status updated to DELETED successfully!");
        return "Member deleted successfully!";
    }
}