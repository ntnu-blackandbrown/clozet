package stud.ntnu.no.backend.User.Service;

import stud.ntnu.no.backend.User.DTOs.RegisterUserDTO;
import stud.ntnu.no.backend.User.DTOs.StatusUserDTO;
import stud.ntnu.no.backend.User.DTOs.UpdateUserDTO;
import stud.ntnu.no.backend.User.DTOs.UserDTO;

import java.util.List;

public interface UserService {
    List<StatusUserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(RegisterUserDTO registerUserDTO);
    UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);
    void deleteUser(Long id);
}