package stud.ntnu.no.backend.ShippingOption.Mapper;

import org.mapstruct.*;
import stud.ntnu.no.backend.ShippingOption.DTOs.CreateShippingOptionDTO;
import stud.ntnu.no.backend.ShippingOption.DTOs.ShippingOptionDTO;
import stud.ntnu.no.backend.ShippingOption.Entity.ShippingOption;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShippingOptionMapper {
    ShippingOptionDTO toDto(ShippingOption shippingOption);
    List<ShippingOptionDTO> toDtoList(List<ShippingOption> shippingOptions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    ShippingOption toEntity(CreateShippingOptionDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateShippingOptionFromDto(CreateShippingOptionDTO dto, @MappingTarget ShippingOption shippingOption);
}