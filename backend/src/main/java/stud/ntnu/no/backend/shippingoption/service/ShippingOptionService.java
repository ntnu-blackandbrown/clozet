package stud.ntnu.no.backend.shippingoption.service;

import java.util.List;
import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;

/**
 * Service interface for managing shipping options.
 * <p>
 * This interface defines methods for CRUD operations on shipping options.
 */
public interface ShippingOptionService {

  /**
   * Retrieves all shipping options.
   *
   * @return a list of ShippingOptionDTOs
   */
  List<ShippingOptionDTO> getAllShippingOptions();

  /**
   * Retrieves a shipping option by its ID.
   *
   * @param id the ID of the shipping option
   * @return the ShippingOptionDTO
   */
  ShippingOptionDTO getShippingOption(Long id);

  /**
   * Creates a new shipping option.
   *
   * @param shippingOptionDTO the CreateShippingOptionDTO
   * @return the created ShippingOptionDTO
   */
  ShippingOptionDTO createShippingOption(CreateShippingOptionDTO shippingOptionDTO);

  /**
   * Updates an existing shipping option.
   *
   * @param id                the ID of the shipping option to update
   * @param shippingOptionDTO the CreateShippingOptionDTO with updated information
   * @return the updated ShippingOptionDTO
   */
  ShippingOptionDTO updateShippingOption(Long id, CreateShippingOptionDTO shippingOptionDTO);

  /**
   * Deletes a shipping option by its ID.
   *
   * @param id the ID of the shipping option to delete
   */
  void deleteShippingOption(Long id);
}