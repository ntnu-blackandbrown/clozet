package stud.ntnu.no.backend.user.service;

import stud.ntnu.no.backend.user.dto.ChangePasswordDTO;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.entity.User;

import java.util.List;

/**
 * Service interface for managing users.
 */
public abstract class UserService {
    public abstract List<UserDTO> getAllUsers();
    public abstract UserDTO getUserById(Long id);
    public abstract UserDTO getUserByUsername(String username);
    public abstract UserDTO createUser(RegisterUserDTO registerUserDTO);
    public abstract void createUserAndSendVerificationEmail(RegisterUserDTO registerUserDTO);
    public abstract UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);
    public abstract void deleteUser(Long id);
    public abstract UserDTO login(LoginDTO loginDTO);
    public abstract void changePassword(String username, ChangePasswordDTO changePasswordDTO);
    public abstract User getCurrentUser();
}