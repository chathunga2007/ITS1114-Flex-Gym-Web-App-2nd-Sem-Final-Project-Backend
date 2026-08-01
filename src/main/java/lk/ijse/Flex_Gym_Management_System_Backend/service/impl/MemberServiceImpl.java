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
        log.info("Execute saveMember() for: {}", memberDTO.getMemberFullName());
        try {
            if (memberDTO.getMemberFullName() == null || memberDTO.getMemberPhoneNumber() == null || memberDTO.getEmail() == null) {
                throw new RuntimeException("Name, Phone, and Email are required!");
            }

            if (userRepository.existsByEmail(memberDTO.getEmail())) {
                throw new RuntimeException("This email is already registered in the system!");
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

            return convertToDTO(savedMember);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public MemberDTO updateMember(Long memberId, MemberDTO memberDTO) {
        log.info("Execute updateMember()");
        try {
            if (memberId == null) {
                throw new RuntimeException("Member ID cannot be null for update!");
            }

            Optional<Member> optionalMember = memberRepository.findById(memberId);
            if (optionalMember.isEmpty()) {
                throw new RuntimeException("Member not found with ID: " + memberId);
            }

            Member member = optionalMember.get();

            if (member.getMemberStatus() == MemberStatus.DELETED) {
                throw new RuntimeException("Cannot update a deleted member!");
            }

            member.setMemberFullName(memberDTO.getMemberFullName());
            member.setMemberPhoneNumber(memberDTO.getMemberPhoneNumber());
            member.setAge(memberDTO.getAge());
            member.setGender(memberDTO.getGender());
            member.setHeightCm(memberDTO.getHeightCm());
            member.setWeightKg(memberDTO.getWeightKg());

            Member updatedMember = memberRepository.save(member);
            log.info("Member updated successfully!");

            return convertToDTO(updatedMember);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public MemberDTO getMemberById(Long id) {
        log.info("Execute getMemberById()");
        try {
            if (id == null) {
                throw new RuntimeException("Member ID cannot be null!");
            }

            Optional<Member> optionalMember = memberRepository.findById(id);

            if (optionalMember.isEmpty()) {
                throw new RuntimeException("Member not found with ID: " + id);
            }

            Member member = optionalMember.get();

            if (member.getMemberStatus() == MemberStatus.DELETED) {
                throw new RuntimeException("Member not found with ID: " + id);
            }

            return convertToDTO(member);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<MemberDTO> getAllActiveMembers() {
        log.info("Execute getAllActiveMembers()");
        try {
            List<Member> memberList = memberRepository.findAllByMemberStatus(MemberStatus.ACTIVE);
            List<MemberDTO> dtoList = new ArrayList<>();

            for (Member member : memberList) {
                dtoList.add(convertToDTO(member));
            }
            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String deleteMember(Long id) {
        log.info("Execute deleteMember()");
        try {
            if (id == null) {
                throw new RuntimeException("Member ID cannot be null!");
            }

            Optional<Member> optionalMember = memberRepository.findById(id);

            if (optionalMember.isEmpty()) {
                throw new RuntimeException("Member not found with ID: " + id);
            }

            Member member = optionalMember.get();

            if (member.getMemberStatus() == MemberStatus.DELETED) {
                throw new RuntimeException("Member is already deleted!");
            }

            member.setMemberStatus(MemberStatus.DELETED);
            memberRepository.save(member);

            log.info("Member status updated to DELETED successfully!");
            return "Member deleted successfully!";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // mapped by entity and dto
    private MemberDTO convertToDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(member.getMemberId());
        dto.setMemberFullName(member.getMemberFullName());
        dto.setMemberPhoneNumber(member.getMemberPhoneNumber());

        if (member.getUser() != null) {
            dto.setEmail(member.getUser().getEmail());
        }

        dto.setAge(member.getAge());
        dto.setGender(member.getGender());
        dto.setHeightCm(member.getHeightCm());
        dto.setWeightKg(member.getWeightKg());
        dto.setMemberStatus(member.getMemberStatus());
        return dto;
    }
}