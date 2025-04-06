package stud.ntnu.no.backend.shippingoption.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.service.ShippingOptionService;

import java.util.List;

/**
 * REST controller for managing shipping options.
 * <p>
 * This controller provides endpoints for CRUD operations on shipping options.
 */
@RestController
@RequestMapping("/api/shipping-options")
public class ShippingOptionController {

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
        return ResponseEntity.ok(shippingOptionService.getShippingOption(id));
    }

    /**
     * Creates a new shipping option.
     *
     * @param shippingOptionDTO the CreateShippingOptionDTO
     * @return the created ShippingOptionDTO
     */
    @PostMapping
    public ResponseEntity<ShippingOptionDTO> createShippingOption(@RequestBody CreateShippingOptionDTO shippingOptionDTO) {
        return new ResponseEntity<>(shippingOptionService.createShippingOption(shippingOptionDTO), HttpStatus.CREATED);
    }

    /**
     * Updates an existing shipping option.
     *
     * @param id the ID of the shipping option to update
     * @param shippingOptionDTO the CreateShippingOptionDTO with updated information
     * @return the updated ShippingOptionDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShippingOptionDTO> updateShippingOption(
            @PathVariable Long id,
            @RequestBody CreateShippingOptionDTO shippingOptionDTO) {
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
        shippingOptionService.deleteShippingOption(id);
        return ResponseEntity.noContent().build();
    }
}