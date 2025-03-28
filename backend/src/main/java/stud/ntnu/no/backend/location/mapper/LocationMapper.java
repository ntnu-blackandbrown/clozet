package stud.ntnu.no.backend.location.mapper;

import org.mapstruct.*;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.entity.Location;

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