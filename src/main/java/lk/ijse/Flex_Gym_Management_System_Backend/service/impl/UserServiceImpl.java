package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.MemberDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.UserDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.User;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.MemberStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.UserRole;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.UserStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.UserRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        log.info("Execute Save User!");
        try {
            if (userDTO.getUserRole() == UserRole.ROLE_MEMBER) {
                if (userDTO.getMemberDTO() == null) {
                    throw new RuntimeException("Member details are required for ROLE_MEMBER!");
                }
            }

            User user = new User();
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            user.setUserRole(userDTO.getUserRole());
            user.setStatus(UserStatus.ACTIVE);

            if (userDTO.getUserRole() == UserRole.ROLE_MEMBER) {
                Member member = new Member();
                member.setMemberFullName(userDTO.getMemberDTO().getMemberFullName());
                member.setMemberPhoneNumber(userDTO.getMemberDTO().getMemberPhoneNumber());
                member.setAge(userDTO.getMemberDTO().getAge());
                member.setGender(userDTO.getMemberDTO().getGender());
                member.setHeightCm(userDTO.getMemberDTO().getHeightCm());
                member.setWeightKg(userDTO.getMemberDTO().getWeightKg());

                member.setMemberStatus(MemberStatus.ACTIVE);

                member.setUser(user);
                user.setMember(member);
            }

            User savedUser = userRepository.save(user);
            log.info("User saved!");

            userDTO.setUserId(savedUser.getUserId());
            return userDTO;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        log.info("Execute Update User!");
        try {
            Optional<User> optionalUser = userRepository.findById(userDTO.getUserId());

            if (optionalUser.isEmpty()) {
                throw new RuntimeException("User not found!");
            }

            User user = optionalUser.get();

            if (user.getStatus() == UserStatus.DELETED) {
                throw new RuntimeException("Cannot update a deleted user!");
            }

            if (userDTO.getUserRole() == UserRole.ROLE_MEMBER && userDTO.getMemberDTO() == null) {
                throw new RuntimeException("Member details are required for ROLE_MEMBER!");
            }

            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            user.setUserRole(userDTO.getUserRole());

            if (userDTO.getUserRole() == UserRole.ROLE_MEMBER) {
                Member member = user.getMember();

                if (member == null) {
                    member = new Member();
                    member.setUser(user);
                    user.setMember(member);
                }

                member.setMemberFullName(userDTO.getMemberDTO().getMemberFullName());
                member.setMemberPhoneNumber(userDTO.getMemberDTO().getMemberPhoneNumber());
                member.setAge(userDTO.getMemberDTO().getAge());
                member.setGender(userDTO.getMemberDTO().getGender());
                member.setHeightCm(userDTO.getMemberDTO().getHeightCm());
                member.setWeightKg(userDTO.getMemberDTO().getWeightKg());

                member.setMemberStatus(MemberStatus.ACTIVE);
            } else {
                user.setMember(null);
            }

            User updatedUser = userRepository.save(user);

            UserDTO responseDTO = new UserDTO();
            responseDTO.setUserId(updatedUser.getUserId());
            responseDTO.setEmail(updatedUser.getEmail());
            responseDTO.setPassword(updatedUser.getPassword());
            responseDTO.setUserRole(updatedUser.getUserRole());

            if (updatedUser.getUserRole() == UserRole.ROLE_MEMBER && updatedUser.getMember() != null) {
                MemberDTO memberDTO = new MemberDTO();
                memberDTO.setMemberId(updatedUser.getMember().getMemberId());
                memberDTO.setMemberFullName(updatedUser.getMember().getMemberFullName());
                memberDTO.setMemberPhoneNumber(updatedUser.getMember().getMemberPhoneNumber());
                memberDTO.setAge(updatedUser.getMember().getAge());
                memberDTO.setGender(updatedUser.getMember().getGender());
                memberDTO.setHeightCm(updatedUser.getMember().getHeightCm());
                memberDTO.setWeightKg(updatedUser.getMember().getWeightKg());

                responseDTO.setMemberDTO(memberDTO);
            }

            log.info("User successfully updated!");
            return responseDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String deleteUser(Long userId) {
        log.info("Execute Soft Delete User for ID: {}", userId);

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = optionalUser.get();

        if (user.getStatus() == UserStatus.DELETED) {
            throw new RuntimeException("User is already deleted!");
        }

        user.setStatus(UserStatus.DELETED);

        if (user.getMember() != null) {
            user.getMember().setMemberStatus(MemberStatus.DELETED);
        }

        userRepository.save(user);

        log.info("User and Member status changed to DELETED successfully!");
        return "User deleted successfully!";
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Execute Get All Users");
        try {
            List<UserDTO> responseList = new ArrayList<>();
            List<User> usersList = userRepository.findAll();

            if (usersList.isEmpty()) {
                throw new RuntimeException("Users list is empty!");
            }

            for (User user : usersList) {
                if (user.getStatus() == UserStatus.DELETED) {
                    continue;
                }

                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(user.getUserId());
                userDTO.setEmail(user.getEmail());
                userDTO.setPassword(user.getPassword());
                userDTO.setUserRole(user.getUserRole());
                userDTO.setStatus(user.getStatus());

                if (user.getUserRole() == UserRole.ROLE_MEMBER && user.getMember() != null) {
                    Member member = user.getMember();

                    if (member.getMemberStatus() != MemberStatus.DELETED) {
                        MemberDTO memberDTO = new MemberDTO();
                        memberDTO.setMemberId(member.getMemberId());
                        memberDTO.setMemberFullName(member.getMemberFullName());
                        memberDTO.setMemberPhoneNumber(member.getMemberPhoneNumber());
                        memberDTO.setAge(member.getAge());
                        memberDTO.setGender(member.getGender());
                        memberDTO.setHeightCm(member.getHeightCm());
                        memberDTO.setWeightKg(member.getWeightKg());
                        memberDTO.setMemberStatus(member.getMemberStatus());

                        userDTO.setMemberDTO(memberDTO);
                    }
                }
                responseList.add(userDTO);
            }
            return responseList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getAllUser(Long userId) {
        log.info("Execute Get User for ID: {}", userId);
        try {
            if (userId == null) {
                throw new RuntimeException("UserId is null!");
            }

            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isEmpty()) {
                throw new RuntimeException("User not found!");
            }

            User user = optionalUser.get();

            if (user.getStatus() == UserStatus.DELETED) {
                throw new RuntimeException("User is already deleted!");
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setEmail(user.getEmail());
            userDTO.setPassword(user.getPassword());
            userDTO.setUserRole(user.getUserRole());
            userDTO.setStatus(user.getStatus());
            if (user.getUserRole() == UserRole.ROLE_MEMBER && user.getMember() != null) {
                Member member = user.getMember();
                if (member.getMemberStatus() != MemberStatus.DELETED) {
                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setMemberId(member.getMemberId());
                    memberDTO.setMemberFullName(member.getMemberFullName());
                    memberDTO.setMemberPhoneNumber(member.getMemberPhoneNumber());
                    memberDTO.setAge(member.getAge());
                    memberDTO.setGender(member.getGender());
                    memberDTO.setHeightCm(member.getHeightCm());
                    memberDTO.setWeightKg(member.getWeightKg());
                    memberDTO.setMemberStatus(member.getMemberStatus());
                    userDTO.setMemberDTO(memberDTO);
                }
            }
            return userDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}