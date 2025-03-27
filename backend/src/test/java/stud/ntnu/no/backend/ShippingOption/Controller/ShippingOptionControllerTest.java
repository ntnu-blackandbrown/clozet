package stud.ntnu.no.backend.ShippingOption.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stud.ntnu.no.backend.ShippingOption.DTOs.CreateShippingOptionDTO;
import stud.ntnu.no.backend.ShippingOption.DTOs.ShippingOptionDTO;
import stud.ntnu.no.backend.ShippingOption.Service.ShippingOptionService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ShippingOptionControllerTest {

    @Mock
    private ShippingOptionService shippingOptionService;

    @InjectMocks
    private ShippingOptionController shippingOptionController;

    private ShippingOptionDTO sampleShippingOptionDTO;
    private ShippingOptionDTO secondShippingOptionDTO;
    private CreateShippingOptionDTO createShippingOptionDTO;
    private List<ShippingOptionDTO> shippingOptionDTOList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        sampleShippingOptionDTO = new ShippingOptionDTO();
        sampleShippingOptionDTO.setId(1L);
        sampleShippingOptionDTO.setName("Standard Shipping");
        sampleShippingOptionDTO.setDescription("5-7 business days");
        sampleShippingOptionDTO.setEstimatedDays(7);
        sampleShippingOptionDTO.setPrice(4.99);
        sampleShippingOptionDTO.setTracked(false);

        secondShippingOptionDTO = new ShippingOptionDTO();
        secondShippingOptionDTO.setId(2L);
        secondShippingOptionDTO.setName("Express Shipping");
        secondShippingOptionDTO.setDescription("1-2 business days");
        secondShippingOptionDTO.setEstimatedDays(2);
        secondShippingOptionDTO.setPrice(12.99);
        secondShippingOptionDTO.setTracked(true);

        shippingOptionDTOList = Arrays.asList(sampleShippingOptionDTO, secondShippingOptionDTO);

        createShippingOptionDTO = new CreateShippingOptionDTO();
        createShippingOptionDTO.setName("Next Day Delivery");
        createShippingOptionDTO.setDescription("Guaranteed next business day delivery");
        createShippingOptionDTO.setEstimatedDays(1);
        createShippingOptionDTO.setPrice(19.99);
        createShippingOptionDTO.setTracked(true);
    }

    @Test
    void getAllShippingOptions_ShouldReturnListOfShippingOptions() {
        when(shippingOptionService.getAllShippingOptions()).thenReturn(shippingOptionDTOList);

        ResponseEntity<List<ShippingOptionDTO>> response = shippingOptionController.getAllShippingOptions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(shippingOptionService, times(1)).getAllShippingOptions();
    }

    @Test
    void getShippingOption_WithValidId_ShouldReturnShippingOption() {
        when(shippingOptionService.getShippingOption(1L)).thenReturn(sampleShippingOptionDTO);

        ResponseEntity<ShippingOptionDTO> response = shippingOptionController.getShippingOption(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Standard Shipping", response.getBody().getName());
        verify(shippingOptionService, times(1)).getShippingOption(1L);
    }

    @Test
    void createShippingOption_WithValidData_ShouldReturnCreatedShippingOption() {
        when(shippingOptionService.createShippingOption(any(CreateShippingOptionDTO.class))).thenReturn(sampleShippingOptionDTO);

        ResponseEntity<ShippingOptionDTO> response = shippingOptionController.createShippingOption(createShippingOptionDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(shippingOptionService, times(1)).createShippingOption(any(CreateShippingOptionDTO.class));
    }

    @Test
    void updateShippingOption_WithValidData_ShouldReturnUpdatedShippingOption() {
        when(shippingOptionService.updateShippingOption(eq(1L), any(CreateShippingOptionDTO.class))).thenReturn(sampleShippingOptionDTO);

        ResponseEntity<ShippingOptionDTO> response = shippingOptionController.updateShippingOption(1L, createShippingOptionDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(shippingOptionService, times(1)).updateShippingOption(eq(1L), any(CreateShippingOptionDTO.class));
    }

    @Test
    void deleteShippingOption_ShouldReturnNoContent() {
        doNothing().when(shippingOptionService).deleteShippingOption(1L);

        ResponseEntity<Void> response = shippingOptionController.deleteShippingOption(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(shippingOptionService, times(1)).deleteShippingOption(1L);
    }
}