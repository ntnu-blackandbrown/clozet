package stud.ntnu.no.backend.user.service;

import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.StatusUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<StatusUserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(RegisterUserDTO registerUserDTO);
    UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);
    void deleteUser(Long id);
    UserDTO login(LoginDTO loginDTO);
}