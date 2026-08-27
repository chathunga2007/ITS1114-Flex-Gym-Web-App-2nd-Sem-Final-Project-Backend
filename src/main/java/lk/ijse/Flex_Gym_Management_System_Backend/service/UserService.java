package lk.ijse.Flex_Gym_Management_System_Backend.service;

import lk.ijse.Flex_Gym_Management_System_Backend.dto.UserDTO;
import java.util.List;

public interface UserService {
    UserDTO saveUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
    String deleteUser(Long userId);
    List<UserDTO> getAllUsers();
    UserDTO getAllUser(Long userId);
    UserDTO getUserDetails(String email, String password);
}