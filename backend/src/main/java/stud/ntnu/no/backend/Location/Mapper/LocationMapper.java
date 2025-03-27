package stud.ntnu.no.backend.Location.Mapper;

import org.mapstruct.*;
import stud.ntnu.no.backend.Location.DTOs.CreateLocationDTO;
import stud.ntnu.no.backend.Location.DTOs.LocationDTO;
import stud.ntnu.no.backend.Location.Entity.Location;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "name", expression = "java(location.getCity() + \", \" + location.getRegion())")
    LocationDTO toDto(Location location);

    List<LocationDTO> toDtoList(List<Location> locations);

    @Mapping(target = "id", ignore = true)
    Location toEntity(CreateLocationDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateLocationFromDto(CreateLocationDTO dto, @MappingTarget Location location);
}