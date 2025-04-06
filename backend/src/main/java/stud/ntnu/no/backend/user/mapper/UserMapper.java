package stud.ntnu.no.backend.user.mapper;

import org.mapstruct.Mapper;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.entity.User;

/**
 * Maps user entities to DTOs and vice versa.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Maps a User entity to a UserDTO.
     *
     * @param user the user entity
     * @return the user DTO
     */
    UserDTO toDto(User user);

    /**
     * Maps a UserDTO to a User entity.
     *
     * @param userDto the user DTO
     * @return the user entity
     */
    User toEntity(RegisterUserDTO dto);
}
