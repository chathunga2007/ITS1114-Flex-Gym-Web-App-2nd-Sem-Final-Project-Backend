package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.FitnessProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FitnessProgressRepository extends JpaRepository<FitnessProgress, Long> {
    List<FitnessProgress> findByMember_MemberIdOrderByRecordDateDesc(Long memberId);
    Optional<FitnessProgress> findTopByMember_MemberIdOrderByRecordDateDesc(Long memberId);
    Optional<FitnessProgress> findTopByMember_MemberIdOrderByRecordDateAsc(Long memberId);
}