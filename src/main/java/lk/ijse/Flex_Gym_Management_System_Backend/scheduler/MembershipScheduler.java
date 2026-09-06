package lk.ijse.Flex_Gym_Management_System_Backend.scheduler;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Membership;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.MembershipStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MembershipRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipScheduler {
    private final MembershipRepository membershipRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runDailyMembershipExpiryCheck() {
        log.info("Automatic Daily Membership Expiration & Reminder Job at {}", LocalDateTime.now());
        Map<String, Object> result = processMembershipExpirations();
        log.info("Result: {}", result);
    }

    public Map<String, Object> processMembershipExpirations() {
        LocalDate today = LocalDate.now();
        List<Membership> activeMemberships = membershipRepository.findAllByMembershipStatus(MembershipStatus.ACTIVE);

        int totalChecked = activeMemberships.size();
        int remindersSent = 0;
        int expiredUpdated = 0;

        log.info("Checking {} active memberships against current date {}", totalChecked, today);

        for (Membership membership : activeMemberships) {
            if (membership.getEndDate() == null) continue;

            Member member = membership.getMember();
            String memberEmail = (member != null && member.getUser() != null) ? member.getUser().getEmail() : null;
            String memberName = member != null ? member.getMemberFullName() : "Member";
            String packageName = membership.getGymPackage() != null ? membership.getGymPackage().getPackageName() : "Flex Gym Plan";

            if (membership.getEndDate().isBefore(today) || membership.getEndDate().isEqual(today)) {
                membership.setMembershipStatus(MembershipStatus.EXPIRED);
                membershipRepository.save(membership);
                expiredUpdated++;
                log.info("Membership #{} for {} marked as EXPIRED (ended on {})", membership.getMembershipId(), memberName, membership.getEndDate());

                if (memberEmail != null && !memberEmail.isBlank()) {
                    emailService.sendMembershipExpiredEmail(memberEmail, memberName, packageName, membership.getEndDate().toString());
                }
            }
            else {
                long daysRemaining = ChronoUnit.DAYS.between(today, membership.getEndDate());
                if (daysRemaining > 0 && daysRemaining <= 3) {
                    remindersSent++;
                    log.info("Membership #{} for {} is expiring in {} days on {}", membership.getMembershipId(), memberName, daysRemaining, membership.getEndDate());

                    if (memberEmail != null && !memberEmail.isBlank()) {
                        emailService.sendMembershipExpiryReminderEmail(
                                memberEmail,
                                memberName,
                                packageName,
                                membership.getEndDate().toString(),
                                (int) daysRemaining
                        );
                    }
                }
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("status", "SUCCESS");
        summary.put("executedAt", LocalDateTime.now().toString());
        summary.put("totalActiveChecked", totalChecked);
        summary.put("remindersSent", remindersSent);
        summary.put("expiredUpdated", expiredUpdated);

        return summary;
    }
}