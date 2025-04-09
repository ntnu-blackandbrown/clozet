package stud.ntnu.no.backend.shippingoption.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.service.ShippingOptionService;

/**
 * REST controller for managing shipping options.
 * <p>
 * This controller provides endpoints for CRUD operations on shipping options.
 */
@RestController
@RequestMapping("/api/shipping-options")
public class ShippingOptionController {

  private static final Logger logger = LoggerFactory.getLogger(ShippingOptionController.class);

  private final ShippingOptionService shippingOptionService;

  /**
   * Constructs a new ShippingOptionController with the specified service.
   *
   * @param shippingOptionService the ShippingOptionService
   */
  public ShippingOptionController(ShippingOptionService shippingOptionService) {
    this.shippingOptionService = shippingOptionService;
  }

  /**
   * Retrieves all shipping options.
   *
   * @return a list of ShippingOptionDTOs
   */
  @GetMapping
  public ResponseEntity<List<ShippingOptionDTO>> getAllShippingOptions() {
    logger.info("Received request to fetch all shipping options");
    return ResponseEntity.ok(shippingOptionService.getAllShippingOptions());
  }

  /**
   * Retrieves a shipping option by its ID.
   *
   * @param id the ID of the shipping option
   * @return the ShippingOptionDTO
   */
  @GetMapping("/{id}")
  public ResponseEntity<ShippingOptionDTO> getShippingOption(@PathVariable Long id) {
    logger.info("Received request to fetch shipping option with id: {}", id);
    return ResponseEntity.ok(shippingOptionService.getShippingOption(id));
  }

  /**
   * Creates a new shipping option.
   *
   * @param shippingOptionDTO the CreateShippingOptionDTO
   * @return the created ShippingOptionDTO
   */
  @PostMapping
  public ResponseEntity<ShippingOptionDTO> createShippingOption(
      @RequestBody CreateShippingOptionDTO shippingOptionDTO) {
    logger.info("Received request to create a new shipping option with name: {}",
        shippingOptionDTO.getName());
    return new ResponseEntity<>(shippingOptionService.createShippingOption(shippingOptionDTO),
        HttpStatus.CREATED);
  }

  /**
   * Updates an existing shipping option.
   *
   * @param id                the ID of the shipping option to update
   * @param shippingOptionDTO the CreateShippingOptionDTO with updated information
   * @return the updated ShippingOptionDTO
   */
  @PutMapping("/{id}")
  public ResponseEntity<ShippingOptionDTO> updateShippingOption(
      @PathVariable Long id,
      @RequestBody CreateShippingOptionDTO shippingOptionDTO) {
    logger.info("Received request to update shipping option with id: {}", id);
    return ResponseEntity.ok(shippingOptionService.updateShippingOption(id, shippingOptionDTO));
  }

  /**
   * Deletes a shipping option by its ID.
   *
   * @param id the ID of the shipping option to delete
   * @return a ResponseEntity with status 204 (No Content)
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteShippingOption(@PathVariable Long id) {
    logger.info("Received request to delete shipping option with id: {}", id);
    shippingOptionService.deleteShippingOption(id);
    return ResponseEntity.noContent().build();
  }
}
