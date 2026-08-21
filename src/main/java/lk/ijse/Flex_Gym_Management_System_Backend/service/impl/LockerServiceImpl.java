package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.LockerDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Locker;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.LockerStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
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
        log.info("Execute saveLocker()");
        if (lockerDTO == null) {
            throw new CustomException(400, "Locker data cannot be null!");
        }
        if (lockerDTO.getLockerNumber() == null || lockerDTO.getLockerNumber().trim().isEmpty()) {
            throw new CustomException(400, "Locker number cannot be empty!");
        }

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
            } else {
                throw new CustomException(404, "Member not found with ID: " + lockerDTO.getMemberId());
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
        log.info("Execute updateLocker()");
        if (lockerDTO == null) {
            throw new CustomException(400, "Locker data cannot be null!");
        }
        if (lockerDTO.getLockerId() == null) {
            throw new CustomException(400, "Locker ID cannot be null for update!");
        }

        Optional<Locker> optionalLocker = lockerRepository.findById(lockerDTO.getLockerId());
        if (optionalLocker.isEmpty()) {
            throw new CustomException(404, "Locker not found with ID: " + lockerDTO.getLockerId());
        }

        Locker locker = optionalLocker.get();
        if (locker.getStatus() == LockerStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted locker!");
        }

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
            } else {
                throw new CustomException(404, "Member not found with ID: " + lockerDTO.getMemberId());
            }
        } else {
            locker.setMember(null);
        }

        lockerRepository.save(locker);
        log.info("Locker updated successfully!");

        return lockerDTO;
    }

    @Override
    public String deleteLocker(Long id) {
        log.info("Execute deleteLocker() for ID: " + id);
        if (id == null) {
            throw new CustomException(400, "Locker ID cannot be null!");
        }

        Optional<Locker> optionalLocker = lockerRepository.findById(id);
        if (optionalLocker.isEmpty()) {
            throw new CustomException(404, "Locker not found with ID: " + id);
        }

        Locker locker = optionalLocker.get();
        if (locker.getStatus() == LockerStatus.DELETED) {
            throw new CustomException(400, "Locker is already deleted!");
        }

        locker.setStatus(LockerStatus.DELETED);
        lockerRepository.save(locker);

        log.info("Locker marked as DELETED successfully!");
        return "Locker deleted successfully!";
    }

    @Override
    public List<LockerDTO> getAllLockers() {
        log.info("Execute getAllLockers()");
        List<Locker> lockerList = lockerRepository.findAll();
        List<LockerDTO> dtoList = new ArrayList<>();

        for (Locker lk : lockerList) {
            if (lk.getStatus() != LockerStatus.DELETED) {
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
        }
        return dtoList;
    }

    @Override
    public LockerDTO getLockerById(Long id) {
        log.info("Execute getLockerById()");
        if (id == null) {
            throw new CustomException(400, "Locker ID cannot be null!");
        }

        Optional<Locker> optionalLocker = lockerRepository.findById(id);
        if (optionalLocker.isEmpty()) {
            throw new CustomException(404, "Locker not found with ID: " + id);
        }

        Locker lk = optionalLocker.get();
        if (lk.getStatus() == LockerStatus.DELETED) {
            throw new CustomException(404, "Locker not found with ID: " + id);
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