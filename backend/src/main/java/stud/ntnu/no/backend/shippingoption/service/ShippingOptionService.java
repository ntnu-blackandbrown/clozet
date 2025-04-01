package stud.ntnu.no.backend.shippingoption.service;

import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;

import java.util.List;

public interface ShippingOptionService {
    List<ShippingOptionDTO> getAllShippingOptions();
    ShippingOptionDTO getShippingOption(Long id);
    ShippingOptionDTO createShippingOption(CreateShippingOptionDTO shippingOptionDTO);
    ShippingOptionDTO updateShippingOption(Long id, CreateShippingOptionDTO shippingOptionDTO);
    void deleteShippingOption(Long id);
}