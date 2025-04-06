package stud.ntnu.no.backend.location.mapper;

import org.mapstruct.*;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.entity.Location;

import java.util.List;

/**
 * Mapper for converting between Location entities and DTOs.
 * <p>
 * This interface uses MapStruct to generate the implementation.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper {

    /**
     * Converts a Location entity to a LocationDTO.
     *
     * @param location the Location entity
     * @return the LocationDTO
     */
    @Mapping(target = "name", expression = "java(location.getCity() + \", \" + location.getRegion())")
    LocationDTO toDto(Location location);

    /**
     * Converts a list of Location entities to a list of LocationDTOs.
     *
     * @param locations the list of Location entities
     * @return the list of LocationDTOs
     */
    List<LocationDTO> toDtoList(List<Location> locations);

    /**
     * Converts a CreateLocationDTO to a Location entity.
     *
     * @param dto the CreateLocationDTO
     * @return the Location entity
     */
    @Mapping(target = "id", ignore = true)
    Location toEntity(CreateLocationDTO dto);

    /**
     * Updates a Location entity from a CreateLocationDTO.
     *
     * @param dto the CreateLocationDTO
     * @param location the Location entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateLocationFromDto(CreateLocationDTO dto, @MappingTarget Location location);
}