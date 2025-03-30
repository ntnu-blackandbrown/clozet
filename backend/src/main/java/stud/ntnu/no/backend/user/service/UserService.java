package stud.ntnu.no.backend.user.service;

import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;

import java.util.List;

public abstract class UserService {
    public abstract List<UserDTO> getAllUsers();
    public abstract UserDTO getUserById(Long id);
    public abstract UserDTO getUserByUsername(String username);
    public abstract UserDTO createUser(RegisterUserDTO registerUserDTO);
    public abstract void createUserAndSendVerificationEmail(RegisterUserDTO registerUserDTO);
    public abstract UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);
    public abstract void deleteUser(Long id);
    public abstract UserDTO login(LoginDTO loginDTO);
}
