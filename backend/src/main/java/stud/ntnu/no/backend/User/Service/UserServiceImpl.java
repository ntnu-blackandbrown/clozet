package stud.ntnu.no.backend.User.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.User.DTOs.RegisterUserDTO;
import stud.ntnu.no.backend.User.DTOs.StatusUserDTO;
import stud.ntnu.no.backend.User.DTOs.UpdateUserDTO;
import stud.ntnu.no.backend.User.DTOs.UserDTO;
import stud.ntnu.no.backend.User.Entity.User;
import stud.ntnu.no.backend.User.Mapper.UserMapper;
import stud.ntnu.no.backend.User.Repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    
    @Override
    public List<StatusUserDTO> getAllUsers() {
        return userMapper.toStatusDtoList(userRepository.findAll());
    }
    
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        return userMapper.toDto(user);
    }
    
    @Override
    @Transactional
    public UserDTO createUser(RegisterUserDTO registerUserDTO) {
        User user = userMapper.toEntity(registerUserDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

        // Update email if provided
        if (updateUserDTO.getEmail() != null) {
            existingUser.setEmail(updateUserDTO.getEmail());
        }

        // Update firstName if provided
        if (updateUserDTO.getFirstName() != null) {
            existingUser.setFirstName(updateUserDTO.getFirstName());
        }

        // Update lastName if provided
        if (updateUserDTO.getLastName() != null) {
            existingUser.setLastName(updateUserDTO.getLastName());
        }

        // Update username if provided
        if (updateUserDTO.getUsername() != null) {
            existingUser.setUsername(updateUserDTO.getUsername());
        }

        // Update role if provided
        if (updateUserDTO.getRole() != null) {
            existingUser.setRole(updateUserDTO.getRole());
        }

        // Update active status
        existingUser.setActive(updateUserDTO.isActive());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}