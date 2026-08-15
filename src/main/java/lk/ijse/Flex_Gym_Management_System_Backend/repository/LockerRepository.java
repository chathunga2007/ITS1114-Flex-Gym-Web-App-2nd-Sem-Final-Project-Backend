package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.Locker;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.LockerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {
    List<Locker> findAllByStatus(LockerStatus status);
}