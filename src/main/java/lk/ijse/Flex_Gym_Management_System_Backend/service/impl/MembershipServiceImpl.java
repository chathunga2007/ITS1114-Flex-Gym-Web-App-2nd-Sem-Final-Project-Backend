package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MembershipDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MembershipRequestDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Membership;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Package;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.MembershipStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PackageStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MembershipRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.PackageRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.MembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        log.info("Execute createMembership() for Member ID: {} and Package ID: {}",
                requestDTO.getMemberId(), requestDTO.getPackageId());
        try {
            if (requestDTO.getMemberId() == null || requestDTO.getPackageId() == null) {
                throw new RuntimeException("Member ID and Package ID cannot be null!");
            }

            Optional<Member> optionalMember = memberRepository.findById(requestDTO.getMemberId());
            if (optionalMember.isEmpty()) {
                throw new RuntimeException("Member not found with ID: " + requestDTO.getMemberId());
            }

            Optional<Package> optionalPackage = packageRepository.findById(requestDTO.getPackageId());
            if (optionalPackage.isEmpty()) {
                throw new RuntimeException("Package not found with ID: " + requestDTO.getPackageId());
            }

            Member member = optionalMember.get();
            Package pkg = optionalPackage.get();

            if (pkg.getPackageStatus() == PackageStatus.DELETED) {
                throw new RuntimeException("Cannot assign a deleted package!");
            }

            Optional<Membership> activeMembership = membershipRepository.findByMemberAndMembershipStatus(member, MembershipStatus.ACTIVE);
            if (activeMembership.isPresent()) {
                throw new RuntimeException("Member already has an ACTIVE membership!");
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

            return convertToDTO(savedMembership);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public MembershipDTO updateMembership(Long membershipId, MembershipRequestDTO requestDTO) {
        log.info("Execute updateMembership() for ID: {}", membershipId);

        try {
            if (membershipId == null) {
                throw new RuntimeException("Membership ID cannot be null for update!");
            }

            if (requestDTO.getMemberId() == null || requestDTO.getPackageId() == null) {
                throw new RuntimeException("Member ID and Package ID cannot be null!");
            }


            Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);

            if (optionalMembership.isEmpty()) {
                throw new RuntimeException("Membership not found with ID: " + membershipId);
            }

            Membership membership = optionalMembership.get();

            if (!membership.getMember().getMemberId().equals(requestDTO.getMemberId())) {
                throw new RuntimeException("Cannot change the Member of an existing membership!");
            }

            if (!membership.getGymPackage().getPackageId().equals(requestDTO.getPackageId())) {
                Optional<Package> optionalPackage = packageRepository.findById(requestDTO.getPackageId());
                if (optionalPackage.isEmpty()) {
                    throw new RuntimeException("Package not found with ID: " + requestDTO.getPackageId());
                }

                Package newPackage = optionalPackage.get();
                membership.setGymPackage(newPackage);
                membership.setEndDate(membership.getStartDate().plusMonths(newPackage.getDurationMonths()));
            }

            Membership updatedMembership = membershipRepository.save(membership);
            log.info("Membership updated successfully!");

            return convertToDTO(updatedMembership);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public MembershipDTO getMembershipById(Long id) {
        log.info("Execute Get Membership By ID: {}", id);
        try {
            if (id == null) {
                throw new RuntimeException("Membership ID cannot be null!");
            }

            Optional<Membership> optionalMembership = membershipRepository.findById(id);

            if (optionalMembership.isEmpty()) {
                throw new RuntimeException("Membership not found with ID: " + id);
            }

            Membership membership = optionalMembership.get();

            if (membership.getMembershipStatus() == MembershipStatus.DELETED) {
                throw new RuntimeException("Membership not found with ID: " + id);
            }

            return convertToDTO(membership);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<MembershipDTO> getMembershipsByMemberId(Long memberId) {
        log.info("Execute Get Memberships By Member ID: {}", memberId);
        try {
            Optional<Member> optionalMember = memberRepository.findById(memberId);
            if (optionalMember.isEmpty()) {
                throw new RuntimeException("Member not found with ID: " + memberId);
            }

            List<Membership> membershipList = membershipRepository.findAllByMember(optionalMember.get());
            List<MembershipDTO> dtoList = new ArrayList<>();

            for (Membership membership : membershipList) {
                dtoList.add(convertToDTO(membership));
            }
            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<MembershipDTO> getAllActiveMemberships() {
        log.info("Execute Get All Active Memberships");
        try {
            List<Membership> membershipList = membershipRepository.findAllByMembershipStatus(MembershipStatus.ACTIVE);
            List<MembershipDTO> dtoList = new ArrayList<>();

            for (Membership membership : membershipList) {
                dtoList.add(convertToDTO(membership));
            }
            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String deleteMembership(Long id) {
        log.info("Execute deleteMembership() for ID: {}", id);
        try {
            if (id == null) {
                throw new RuntimeException("Membership ID cannot be null!");
            }

            Optional<Membership> optionalMembership = membershipRepository.findById(id);

            if (optionalMembership.isEmpty()) {
                throw new RuntimeException("Membership not found with ID: " + id);
            }

            Membership membership = optionalMembership.get();

            if (membership.getMembershipStatus() == MembershipStatus.DELETED) {
                throw new RuntimeException("Membership is already deleted!");
            }

            membership.setMembershipStatus(MembershipStatus.DELETED);
            membershipRepository.save(membership);

            log.info("Membership status changed to DELETED successfully!");
            return "Membership deleted successfully!";

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private MembershipDTO convertToDTO(Membership membership) {
        MembershipDTO dto = new MembershipDTO();
        dto.setMembershipId(membership.getMembershipId());
        dto.setStartDate(membership.getStartDate());
        dto.setEndDate(membership.getEndDate());
        dto.setMembershipStatus(membership.getMembershipStatus());

        if (membership.getMember() != null) {
            dto.setMemberId(membership.getMember().getMemberId());
            dto.setMemberName(membership.getMember().getMemberFullName());
        }

        if (membership.getGymPackage() != null) {
            dto.setPackageId(membership.getGymPackage().getPackageId());
            dto.setPackageName(membership.getGymPackage().getPackageName());
        }

        return dto;
    }
}