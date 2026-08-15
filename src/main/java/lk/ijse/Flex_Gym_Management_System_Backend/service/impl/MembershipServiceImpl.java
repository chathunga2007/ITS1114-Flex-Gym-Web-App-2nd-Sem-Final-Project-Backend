package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MembershipDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MembershipRequestDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Membership;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Package;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.MembershipStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PackageStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MembershipRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.PackageRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.MembershipService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class MembershipServiceImpl implements MembershipService {
    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;
    private final PackageRepository packageRepository;

    public MembershipServiceImpl(MembershipRepository membershipRepository, MemberRepository memberRepository, PackageRepository packageRepository) {
        this.membershipRepository = membershipRepository;
        this.memberRepository = memberRepository;
        this.packageRepository = packageRepository;
    }

    @Override
    public MembershipDTO createMembership(MembershipRequestDTO requestDTO) {
        log.info("Execute createMembership()");

        if (requestDTO == null) {
            throw new CustomException(400, "Membership request data cannot be null!");
        }
        if (requestDTO.getMemberId() == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }
        if (requestDTO.getPackageId() == null) {
            throw new CustomException(400, "Package ID cannot be null!");
        }

        Optional<Member> optionalMember = memberRepository.findById(requestDTO.getMemberId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + requestDTO.getMemberId());
        }

        Optional<Package> optionalPackage = packageRepository.findById(requestDTO.getPackageId());
        if (optionalPackage.isEmpty()) {
            throw new CustomException(404, "Package not found with ID: " + requestDTO.getPackageId());
        }

        Member member = optionalMember.get();
        Package pkg = optionalPackage.get();

        if (pkg.getPackageStatus() == PackageStatus.DELETED) {
            throw new CustomException(400, "Cannot assign a deleted package!");
        }

        Optional<Membership> activeMembership = membershipRepository.findByMemberAndMembershipStatus(member, MembershipStatus.ACTIVE);
        if (activeMembership.isPresent()) {
            throw new CustomException(409, "Member already has an ACTIVE membership!");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(pkg.getDurationMonths());

        Membership membership = new Membership();
        membership.setMember(member);
        membership.setGymPackage(pkg);
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setMembershipStatus(MembershipStatus.ACTIVE);

        Membership savedMembership = membershipRepository.save(membership);
        log.info("Membership saved successfully!");

        MembershipDTO responseDTO = new MembershipDTO();
        responseDTO.setMembershipId(savedMembership.getMembershipId());
        responseDTO.setStartDate(savedMembership.getStartDate());
        responseDTO.setEndDate(savedMembership.getEndDate());
        responseDTO.setMembershipStatus(savedMembership.getMembershipStatus());

        if (savedMembership.getMember() != null) {
            responseDTO.setMemberId(savedMembership.getMember().getMemberId());
            responseDTO.setMemberName(savedMembership.getMember().getMemberFullName());
        }

        if (savedMembership.getGymPackage() != null) {
            responseDTO.setPackageId(savedMembership.getGymPackage().getPackageId());
            responseDTO.setPackageName(savedMembership.getGymPackage().getPackageName());
        }

        return responseDTO;
    }

    @Override
    public MembershipDTO updateMembership(Long membershipId, MembershipRequestDTO requestDTO) {
        log.info("Execute updateMembership() for ID: {}", membershipId);

        if (membershipId == null) {
            throw new CustomException(400, "Membership ID cannot be null for update!");
        }
        if (requestDTO == null) {
            throw new CustomException(400, "Membership request data cannot be null!");
        }
        if (requestDTO.getMemberId() == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }
        if (requestDTO.getPackageId() == null) {
            throw new CustomException(400, "Package ID cannot be null!");
        }

        Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);

        if (optionalMembership.isEmpty()) {
            throw new CustomException(404, "Membership not found with ID: " + membershipId);
        }

        Membership membership = optionalMembership.get();

        if (!membership.getMember().getMemberId().equals(requestDTO.getMemberId())) {
            throw new CustomException(400, "Cannot change the Member of an existing membership!");
        }

        if (!membership.getGymPackage().getPackageId().equals(requestDTO.getPackageId())) {
            Optional<Package> optionalPackage = packageRepository.findById(requestDTO.getPackageId());
            if (optionalPackage.isEmpty()) {
                throw new CustomException(404, "Package not found with ID: " + requestDTO.getPackageId());
            }

            Package newPackage = optionalPackage.get();
            membership.setGymPackage(newPackage);
            membership.setEndDate(membership.getStartDate().plusMonths(newPackage.getDurationMonths()));
        }

        Membership updatedMembership = membershipRepository.save(membership);
        log.info("Membership updated successfully!");

        MembershipDTO responseDTO = new MembershipDTO();
        responseDTO.setMembershipId(updatedMembership.getMembershipId());
        responseDTO.setStartDate(updatedMembership.getStartDate());
        responseDTO.setEndDate(updatedMembership.getEndDate());
        responseDTO.setMembershipStatus(updatedMembership.getMembershipStatus());

        if (updatedMembership.getMember() != null) {
            responseDTO.setMemberId(updatedMembership.getMember().getMemberId());
            responseDTO.setMemberName(updatedMembership.getMember().getMemberFullName());
        }

        if (updatedMembership.getGymPackage() != null) {
            responseDTO.setPackageId(updatedMembership.getGymPackage().getPackageId());
            responseDTO.setPackageName(updatedMembership.getGymPackage().getPackageName());
        }

        return responseDTO;
    }

    @Override
    public MembershipDTO getMembershipById(Long id) {
        log.info("Execute Get Membership By ID");
        if (id == null) {
            throw new CustomException(400, "Membership ID cannot be null!");
        }

        Optional<Membership> optionalMembership = membershipRepository.findById(id);

        if (optionalMembership.isEmpty()) {
            throw new CustomException(404, "Membership not found with ID: " + id);
        }

        Membership membership = optionalMembership.get();

        if (membership.getMembershipStatus() == MembershipStatus.DELETED) {
            throw new CustomException(404, "Membership not found with ID: " + id);
        }

        MembershipDTO responseDTO = new MembershipDTO();
        responseDTO.setMembershipId(membership.getMembershipId());
        responseDTO.setStartDate(membership.getStartDate());
        responseDTO.setEndDate(membership.getEndDate());
        responseDTO.setMembershipStatus(membership.getMembershipStatus());

        if (membership.getMember() != null) {
            responseDTO.setMemberId(membership.getMember().getMemberId());
            responseDTO.setMemberName(membership.getMember().getMemberFullName());
        }

        if (membership.getGymPackage() != null) {
            responseDTO.setPackageId(membership.getGymPackage().getPackageId());
            responseDTO.setPackageName(membership.getGymPackage().getPackageName());
        }

        return responseDTO;
    }

    @Override
    public List<MembershipDTO> getMembershipsByMemberId(Long memberId) {
        log.info("Execute Get Memberships By Member ID");
        if (memberId == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + memberId);
        }

        List<Membership> membershipList = membershipRepository.findAllByMember(optionalMember.get());
        List<MembershipDTO> dtoList = new ArrayList<>();

        for (Membership membership : membershipList) {
            MembershipDTO responseDTO = new MembershipDTO();
            responseDTO.setMembershipId(membership.getMembershipId());
            responseDTO.setStartDate(membership.getStartDate());
            responseDTO.setEndDate(membership.getEndDate());
            responseDTO.setMembershipStatus(membership.getMembershipStatus());

            if (membership.getMember() != null) {
                responseDTO.setMemberId(membership.getMember().getMemberId());
                responseDTO.setMemberName(membership.getMember().getMemberFullName());
            }

            if (membership.getGymPackage() != null) {
                responseDTO.setPackageId(membership.getGymPackage().getPackageId());
                responseDTO.setPackageName(membership.getGymPackage().getPackageName());
            }
            dtoList.add(responseDTO);
        }
        return dtoList;
    }

    @Override
    public List<MembershipDTO> getAllActiveMemberships() {
        log.info("Execute Get All Active Memberships");
        List<Membership> membershipList = membershipRepository.findAllByMembershipStatus(MembershipStatus.ACTIVE);
        List<MembershipDTO> dtoList = new ArrayList<>();

        for (Membership membership : membershipList) {
            MembershipDTO responseDTO = new MembershipDTO();
            responseDTO.setMembershipId(membership.getMembershipId());
            responseDTO.setStartDate(membership.getStartDate());
            responseDTO.setEndDate(membership.getEndDate());
            responseDTO.setMembershipStatus(membership.getMembershipStatus());

            if (membership.getMember() != null) {
                responseDTO.setMemberId(membership.getMember().getMemberId());
                responseDTO.setMemberName(membership.getMember().getMemberFullName());
            }

            if (membership.getGymPackage() != null) {
                responseDTO.setPackageId(membership.getGymPackage().getPackageId());
                responseDTO.setPackageName(membership.getGymPackage().getPackageName());
            }
            dtoList.add(responseDTO);
        }
        return dtoList;
    }

    @Override
    public String deleteMembership(Long id) {
        log.info("Execute deleteMembership()");
        if (id == null) {
            throw new CustomException(400, "Membership ID cannot be null!");
        }

        Optional<Membership> optionalMembership = membershipRepository.findById(id);

        if (optionalMembership.isEmpty()) {
            throw new CustomException(404, "Membership not found with ID: " + id);
        }

        Membership membership = optionalMembership.get();

        if (membership.getMembershipStatus() == MembershipStatus.DELETED) {
            throw new CustomException(400, "Membership is already deleted!");
        }

        membership.setMembershipStatus(MembershipStatus.DELETED);
        membershipRepository.save(membership);

        log.info("Membership status changed to DELETED successfully!");
        return "Membership deleted successfully!";
    }
}