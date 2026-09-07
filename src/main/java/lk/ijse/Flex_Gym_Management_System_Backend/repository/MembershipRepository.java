package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Membership;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByMemberAndMembershipStatus(Member member, MembershipStatus status);
    Optional<Membership> findByMember(Member member);
    List<Membership> findAllByMember(Member member);
    List<Membership> findAllByMembershipStatus(MembershipStatus status);
    List<Membership> findAllByMembershipStatusIn(List<MembershipStatus> statuses);
}