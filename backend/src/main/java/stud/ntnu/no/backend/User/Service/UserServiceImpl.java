package stud.ntnu.no.backend.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.StatusUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.exception.AuthenticationException;
import stud.ntnu.no.backend.user.mapper.UserMapper;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
        
        // Kryptere passordet før lagring
        user.setPasswordHash(passwordEncoder.encode(registerUserDTO.getPassword()));
        
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
    
    @Override
    public UserDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
            .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        
        if (!user.isActive()) {
            throw new AuthenticationException("User account is disabled");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }
        
        return userMapper.toDto(user);
    }
}