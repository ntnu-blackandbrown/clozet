package stud.ntnu.no.backend.shippingoption.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.service.ShippingOptionService;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-options")
public class ShippingOptionController {

    private final ShippingOptionService shippingOptionService;

    public ShippingOptionController(ShippingOptionService shippingOptionService) {
        this.shippingOptionService = shippingOptionService;
    }

    @GetMapping
    public ResponseEntity<List<ShippingOptionDTO>> getAllShippingOptions() {
        return ResponseEntity.ok(shippingOptionService.getAllShippingOptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingOptionDTO> getShippingOption(@PathVariable Long id) {
        return ResponseEntity.ok(shippingOptionService.getShippingOption(id));
    }

    @PostMapping
    public ResponseEntity<ShippingOptionDTO> createShippingOption(@RequestBody CreateShippingOptionDTO shippingOptionDTO) {
        return new ResponseEntity<>(shippingOptionService.createShippingOption(shippingOptionDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShippingOptionDTO> updateShippingOption(
            @PathVariable Long id,
            @RequestBody CreateShippingOptionDTO shippingOptionDTO) {
        return ResponseEntity.ok(shippingOptionService.updateShippingOption(id, shippingOptionDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShippingOption(@PathVariable Long id) {
        shippingOptionService.deleteShippingOption(id);
        return ResponseEntity.noContent().build();
    }
}