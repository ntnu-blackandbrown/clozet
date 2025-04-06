package stud.ntnu.no.backend.shippingoption.mapper;

import org.mapstruct.*;
import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;

import java.util.List;

/**
 * Mapper for converting between ShippingOption entities and DTOs.
 * <p>
 * This interface uses MapStruct to generate the implementation.
 */
@Mapper(componentModel = "spring")
public interface ShippingOptionMapper {
    /**
     * Converts a ShippingOption entity to a ShippingOptionDTO.
     *
     * @param shippingOption the ShippingOption entity
     * @return the ShippingOptionDTO
     */
    ShippingOptionDTO toDto(ShippingOption shippingOption);

    /**
     * Converts a list of ShippingOption entities to a list of ShippingOptionDTOs.
     *
     * @param shippingOptions the list of ShippingOption entities
     * @return the list of ShippingOptionDTOs
     */
    List<ShippingOptionDTO> toDtoList(List<ShippingOption> shippingOptions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    ShippingOption toEntity(CreateShippingOptionDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateShippingOptionFromDto(CreateShippingOptionDTO dto, @MappingTarget ShippingOption shippingOption);
}