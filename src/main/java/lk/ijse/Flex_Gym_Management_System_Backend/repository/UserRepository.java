package lk.ijse.Flex_Gym_Management_System_Backend.repository;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}