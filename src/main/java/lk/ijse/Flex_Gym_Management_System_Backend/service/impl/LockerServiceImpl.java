package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.LockerDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Locker;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.LockerStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.LockerRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.LockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class LockerServiceImpl implements LockerService {
    private final LockerRepository lockerRepository;
    private final MemberRepository memberRepository;

    public LockerServiceImpl(LockerRepository lockerRepository, MemberRepository memberRepository) {
        this.lockerRepository = lockerRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public LockerDTO saveLocker(LockerDTO lockerDTO) {
        log.info("Execute Save Locker!");

        if (lockerDTO.getStatus() == null) {
            lockerDTO.setStatus(LockerStatus.AVAILABLE);
        }

        Locker locker = new Locker();
        locker.setLockerNumber(lockerDTO.getLockerNumber());
        locker.setIsOccupied(lockerDTO.getIsOccupied() != null ? lockerDTO.getIsOccupied() : false);
        locker.setStatus(lockerDTO.getStatus());

        if (lockerDTO.getMemberId() != null) {
            Optional<Member> optionalMember = memberRepository.findById(lockerDTO.getMemberId());
            if (optionalMember.isPresent()) {
                locker.setMember(optionalMember.get());
            }
        } else {
            locker.setMember(null);
        }

        Locker savedLocker = lockerRepository.save(locker);
        log.info("Locker saved successfully!");

        lockerDTO.setLockerId(savedLocker.getLockerId());
        return lockerDTO;
    }

    @Override
    public LockerDTO updateLocker(LockerDTO lockerDTO) {
        log.info("Execute Update Locker");

        Optional<Locker> optionalLocker = lockerRepository.findById(lockerDTO.getLockerId());

        if (optionalLocker.isPresent()) {
            Locker locker = optionalLocker.get();
            locker.setLockerNumber(lockerDTO.getLockerNumber());
            if (lockerDTO.getIsOccupied() != null) {
                locker.setIsOccupied(lockerDTO.getIsOccupied());
            }
            if (lockerDTO.getStatus() != null) {
                locker.setStatus(lockerDTO.getStatus());
            }

            if (lockerDTO.getMemberId() != null) {
                Optional<Member> optionalMember = memberRepository.findById(lockerDTO.getMemberId());
                if (optionalMember.isPresent()) {
                    locker.setMember(optionalMember.get());
                }
            } else {
                locker.setMember(null);
            }

            lockerRepository.save(locker);
            log.info("Locker updated successfully!");
        }

        return lockerDTO;
    }

    @Override
    public String deleteLocker(Long id) {
        log.info("Execute Soft Delete Locker for ID: " + id);

        Optional<Locker> optionalLocker = lockerRepository.findById(id);

        if (optionalLocker.isPresent()) {
            Locker locker = optionalLocker.get();
            locker.setStatus(LockerStatus.DELETED);
            lockerRepository.save(locker);
            log.info("Locker marked as DELETED successfully!");
            return "Locker deleted successfully!";
        }
        return "Locker not found!";
    }

    @Override
    public List<LockerDTO> getAllLockers() {
        log.info("Execute Get All Active Lockers");
        List<Locker> lockerList = lockerRepository.findAllByStatus(LockerStatus.DELETED);
        List<LockerDTO> dtoList = new ArrayList<>();

        for (Locker lk : lockerList) {
            LockerDTO dto = new LockerDTO();
            dto.setLockerId(lk.getLockerId());
            dto.setLockerNumber(lk.getLockerNumber());
            dto.setIsOccupied(lk.getIsOccupied());
            dto.setStatus(lk.getStatus());
            if (lk.getMember() != null) {
                dto.setMemberId(lk.getMember().getMemberId());
            }
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public LockerDTO getLockerById(Long id) {
        log.info("Execute Get Locker By ID");

        Optional<Locker> optionalLocker = lockerRepository.findById(id);

        if (optionalLocker.isEmpty()) {
            System.out.println("Locker not found with ID: " + id);
            return null;
        }

        Locker lk = optionalLocker.get();

        if (lk.getStatus() == LockerStatus.DELETED) {
            System.out.println("Locker not found with ID: " + id);
            return null;
        }

        LockerDTO dto = new LockerDTO();
        dto.setLockerId(lk.getLockerId());
        dto.setLockerNumber(lk.getLockerNumber());
        dto.setIsOccupied(lk.getIsOccupied());
        dto.setStatus(lk.getStatus());
        if (lk.getMember() != null) {
            dto.setMemberId(lk.getMember().getMemberId());
        }
        return dto;
    }
}