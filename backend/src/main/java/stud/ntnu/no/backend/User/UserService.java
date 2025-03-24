package stud.ntnu.no.backend.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.User.DTOs.*;
import stud.ntnu.no.backend.User.Exceptions.EmailAlreadyInUseException;
import stud.ntnu.no.backend.User.Exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public List<StatusUserDTO> getAllUsers() {
    return userRepository.findAll().stream().map(this::convertToStatusDTO).collect(Collectors.toList());
  }

  public UserDTO getUserById(Long id) {
    return userRepository.findById(id).map(this::convertToDTO).orElseThrow(() -> new UserNotFoundException(id));
  }

  public UserDTO createUser(RegisterUserDTO registerUserDTO) {
    if (userRepository.existsByEmail(registerUserDTO.getEmail())) {
      throw new EmailAlreadyInUseException(registerUserDTO.getEmail());
    }
    User user = convertToEntity(registerUserDTO);
    user = userRepository.save(user);
    return convertToDTO(user);
  }

  public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    user.setEmail(updateUserDTO.getEmail());
    user.setFirstName(updateUserDTO.getFirstName());
    user.setLastName(updateUserDTO.getLastName());
    user.setUsername(updateUserDTO.getUsername());
    user.setActive(updateUserDTO.isActive());
    user.setRole(updateUserDTO.getRole());
    user = userRepository.save(user);
    return convertToDTO(user);
  }

  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException(id);
    }
    userRepository.deleteById(id);
  }

  private UserDTO convertToDTO(User user) {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(user.getId());
    userDTO.setEmail(user.getEmail());
    userDTO.setFirstName(user.getFirstName());
    userDTO.setLastName(user.getLastName());
    userDTO.setUsername(user.getUsername());
    userDTO.setActive(user.isActive());
    userDTO.setRole(user.getRole());
    return userDTO;
  }

  private StatusUserDTO convertToStatusDTO(User user) {
    StatusUserDTO statusUserDTO = new StatusUserDTO();
    statusUserDTO.setId(user.getId());
    statusUserDTO.setEmail(user.getEmail());
    statusUserDTO.setUsername(user.getUsername());
    statusUserDTO.setActive(user.isActive());
    return statusUserDTO;
  }

  private User convertToEntity(RegisterUserDTO registerUserDTO) {
    User user = new User();
    user.setEmail(registerUserDTO.getEmail());
    user.setFirstName(registerUserDTO.getFirstName());
    user.setLastName(registerUserDTO.getLastName());
    user.setUsername(registerUserDTO.getUsername());
    user.setPasswordHash(registerUserDTO.getPassword()); // Assuming password is hashed elsewhere
    user.setRole(registerUserDTO.getRole());
    user.setActive(true); // Default to active when creating a new user
    return user;
  }
}