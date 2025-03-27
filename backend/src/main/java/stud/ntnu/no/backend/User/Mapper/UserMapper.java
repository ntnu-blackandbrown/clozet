package stud.ntnu.no.backend.User.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stud.ntnu.no.backend.User.DTOs.RegisterUserDTO;
import stud.ntnu.no.backend.User.DTOs.StatusUserDTO;
import stud.ntnu.no.backend.User.DTOs.UserDTO;
import stud.ntnu.no.backend.User.Entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Simple mapping with no subcategory references
    UserDTO toDto(User user);
    StatusUserDTO toStatusDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "phoneNumber", ignore = true)  // Add these mappings
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "fullName", ignore = true)
    User toEntity(RegisterUserDTO registerUserDTO);

    List<StatusUserDTO> toStatusDtoList(List<User> users);
}