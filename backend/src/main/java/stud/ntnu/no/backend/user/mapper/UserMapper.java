package stud.ntnu.no.backend.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    // Map password to passwordHash, assuming you will handle hashing later or in a service
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // Note: the User entity has a boolean field with a getter isActive(), so ignore that field
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    @Mapping(target = "verificationTokenExpiry", ignore = true)
    // User has a setFullName() method which MapStruct sees as a target property. Ignore if not needed.
    @Mapping(target = "fullName", ignore = true)
    User toEntity(RegisterUserDTO dto);
}
