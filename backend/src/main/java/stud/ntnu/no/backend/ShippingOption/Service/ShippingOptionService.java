package stud.ntnu.no.backend.ShippingOption.Service;

import stud.ntnu.no.backend.ShippingOption.DTOs.CreateShippingOptionDTO;
import stud.ntnu.no.backend.ShippingOption.DTOs.ShippingOptionDTO;

import java.util.List;

public interface ShippingOptionService {
    List<ShippingOptionDTO> getAllShippingOptions();
    ShippingOptionDTO getShippingOption(Long id);
    ShippingOptionDTO createShippingOption(CreateShippingOptionDTO shippingOptionDTO);
    ShippingOptionDTO updateShippingOption(Long id, CreateShippingOptionDTO shippingOptionDTO);
    void deleteShippingOption(Long id);
}