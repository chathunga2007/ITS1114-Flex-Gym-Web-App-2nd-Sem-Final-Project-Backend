package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.FitnessProgressDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.FitnessProgress;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.FitnessProgressRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.FitnessProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FitnessProgressServiceImpl implements FitnessProgressService {
    private final FitnessProgressRepository progressRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public FitnessProgressDTO logProgress(FitnessProgressDTO dto) {
        log.info("Logging fitness progress for member ID: {}", dto.getMemberId());

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + dto.getMemberId()));

        FitnessProgress progress = new FitnessProgress();
        progress.setMember(member);
        progress.setRecordDate(dto.getRecordDate() != null ? dto.getRecordDate() : LocalDate.now());
        progress.setWeightKg(dto.getWeightKg());
        progress.setBodyFatPercentage(dto.getBodyFatPercentage());
        progress.setMuscleMassKg(dto.getMuscleMassKg());
        progress.setChestCm(dto.getChestCm());
        progress.setWaistCm(dto.getWaistCm());
        progress.setArmsCm(dto.getArmsCm());
        progress.setNotes(dto.getNotes());

        // Calculate BMI based on Member Height
        if (member.getHeightCm() != null && member.getHeightCm().compareTo(BigDecimal.ZERO) > 0 && dto.getWeightKg() != null) {
            BigDecimal heightInMeters = member.getHeightCm().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal heightSquared = heightInMeters.multiply(heightInMeters);
            if (heightSquared.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal bmi = dto.getWeightKg().divide(heightSquared, 1, RoundingMode.HALF_UP);
                progress.setBmi(bmi);

                double bmiVal = bmi.doubleValue();
                if (bmiVal < 18.5) {
                    progress.setBmiCategory("Underweight");
                } else if (bmiVal < 25.0) {
                    progress.setBmiCategory("Healthy Weight");
                } else if (bmiVal < 30.0) {
                    progress.setBmiCategory("Overweight");
                } else {
                    progress.setBmiCategory("Obese");
                }
            }
        }

        // Determine Milestone Badge based on history
        Optional<FitnessProgress> earliestLog = progressRepository.findTopByMember_MemberIdOrderByRecordDateAsc(member.getMemberId());
        if (earliestLog.isEmpty()) {
            progress.setMilestoneBadge("First Check-In 🎯");
        } else {
            BigDecimal baselineWeight = earliestLog.get().getWeightKg();
            if (baselineWeight != null && dto.getWeightKg() != null) {
                BigDecimal diff = dto.getWeightKg().subtract(baselineWeight);
                if (diff.compareTo(BigDecimal.valueOf(-5.0)) <= 0) {
                    progress.setMilestoneBadge("5kg Shred Legend 🏆");
                } else if (diff.compareTo(BigDecimal.valueOf(-2.0)) <= 0) {
                    progress.setMilestoneBadge("Fat Burner 🔥");
                } else if (diff.compareTo(BigDecimal.valueOf(3.0)) >= 0) {
                    progress.setMilestoneBadge("Muscle Mass Builder 💪");
                } else {
                    progress.setMilestoneBadge("Consistency Champion ⚡");
                }
            } else {
                progress.setMilestoneBadge("Progress Tracker ✓");
            }
        }

        // Synchronize Member entity's current weight
        member.setWeightKg(dto.getWeightKg());
        memberRepository.save(member);

        FitnessProgress saved = progressRepository.save(progress);
        return mapToDTO(saved, earliestLog.map(FitnessProgress::getWeightKg).orElse(saved.getWeightKg()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FitnessProgressDTO> getMemberProgressHistory(Long memberId) {
        List<FitnessProgress> list = progressRepository.findByMember_MemberIdOrderByRecordDateDesc(memberId);
        Optional<FitnessProgress> earliest = progressRepository.findTopByMember_MemberIdOrderByRecordDateAsc(memberId);
        BigDecimal baseline = earliest.map(FitnessProgress::getWeightKg).orElse(BigDecimal.ZERO);

        List<FitnessProgressDTO> dtos = new ArrayList<>();
        for (FitnessProgress fp : list) {
            dtos.add(mapToDTO(fp, baseline));
        }
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public FitnessProgressDTO getLatestProgress(Long memberId) {
        return progressRepository.findTopByMember_MemberIdOrderByRecordDateDesc(memberId)
                .map(fp -> {
                    Optional<FitnessProgress> earliest = progressRepository.findTopByMember_MemberIdOrderByRecordDateAsc(memberId);
                    return mapToDTO(fp, earliest.map(FitnessProgress::getWeightKg).orElse(fp.getWeightKg()));
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteProgress(Long progressId) {
        progressRepository.deleteById(progressId);
    }

    private FitnessProgressDTO mapToDTO(FitnessProgress entity, BigDecimal baselineWeight) {
        FitnessProgressDTO dto = new FitnessProgressDTO();
        dto.setProgressId(entity.getProgressId());
        if (entity.getMember() != null) {
            dto.setMemberId(entity.getMember().getMemberId());
            dto.setMemberName(entity.getMember().getMemberFullName());
        }
        dto.setRecordDate(entity.getRecordDate());
        dto.setWeightKg(entity.getWeightKg());
        dto.setBodyFatPercentage(entity.getBodyFatPercentage());
        dto.setMuscleMassKg(entity.getMuscleMassKg());
        dto.setChestCm(entity.getChestCm());
        dto.setWaistCm(entity.getWaistCm());
        dto.setArmsCm(entity.getArmsCm());
        dto.setBmi(entity.getBmi());
        dto.setBmiCategory(entity.getBmiCategory());
        dto.setMilestoneBadge(entity.getMilestoneBadge());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());

        if (baselineWeight != null && entity.getWeightKg() != null) {
            dto.setWeightChangeKg(entity.getWeightKg().subtract(baselineWeight).setScale(1, RoundingMode.HALF_UP));
        } else {
            dto.setWeightChangeKg(BigDecimal.ZERO);
        }

        return dto;
    }
}