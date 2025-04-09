package stud.ntnu.no.backend.shippingoption.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.shippingoption.exception.ShippingOptionNotFoundException;
import stud.ntnu.no.backend.shippingoption.exception.ShippingOptionValidationException;
import stud.ntnu.no.backend.shippingoption.mapper.ShippingOptionMapper;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;

class ShippingOptionServiceImplTest {

  @Mock private ShippingOptionRepository shippingOptionRepository;

  @Mock private ShippingOptionMapper shippingOptionMapper;

  @InjectMocks private ShippingOptionServiceImpl shippingOptionService;

  private ShippingOption shippingOption;
  private ShippingOptionDTO shippingOptionDTO;
  private CreateShippingOptionDTO createShippingOptionDTO;
  private List<ShippingOption> shippingOptionList;
  private List<ShippingOptionDTO> shippingOptionDTOList;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Setup test data
    shippingOption = new ShippingOption();
    shippingOption.setId(1L);
    shippingOption.setName("Express Shipping");
    shippingOption.setPrice(15.0);
    shippingOption.setEstimatedDays(2);
    shippingOption.setDescription("Fast delivery within 2 days");

    shippingOptionDTO = new ShippingOptionDTO();
    shippingOptionDTO.setId(1L);
    shippingOptionDTO.setName("Express Shipping");
    shippingOptionDTO.setPrice(15.0);
    shippingOptionDTO.setEstimatedDays(2);
    shippingOptionDTO.setDescription("Fast delivery within 2 days");

    createShippingOptionDTO = new CreateShippingOptionDTO();
    createShippingOptionDTO.setName("Express Shipping");
    createShippingOptionDTO.setPrice(15.0);
    createShippingOptionDTO.setEstimatedDays(2);
    createShippingOptionDTO.setDescription("Fast delivery within 2 days");

    ShippingOption shippingOption2 = new ShippingOption();
    shippingOption2.setId(2L);
    shippingOption2.setName("Standard Shipping");
    shippingOption2.setPrice(5.0);
    shippingOption2.setEstimatedDays(5);
    shippingOption2.setDescription("Standard delivery within 5 days");

    ShippingOptionDTO shippingOptionDTO2 = new ShippingOptionDTO();
    shippingOptionDTO2.setId(2L);
    shippingOptionDTO2.setName("Standard Shipping");
    shippingOptionDTO2.setPrice(5.0);
    shippingOptionDTO2.setEstimatedDays(5);
    shippingOptionDTO2.setDescription("Standard delivery within 5 days");

    shippingOptionList = Arrays.asList(shippingOption, shippingOption2);
    shippingOptionDTOList = Arrays.asList(shippingOptionDTO, shippingOptionDTO2);
  }

  @Test
  void getAllShippingOptions() {
    // Arrange
    when(shippingOptionRepository.findAll()).thenReturn(shippingOptionList);
    when(shippingOptionMapper.toDtoList(shippingOptionList)).thenReturn(shippingOptionDTOList);

    // Act
    List<ShippingOptionDTO> result = shippingOptionService.getAllShippingOptions();

    // Assert
    assertEquals(2, result.size());
    assertEquals(shippingOptionDTOList, result);
    verify(shippingOptionRepository, times(1)).findAll();
    verify(shippingOptionMapper, times(1)).toDtoList(shippingOptionList);
  }

  @Test
  void getShippingOption_ExistingId_ReturnsShippingOption() {
    // Arrange
    Long shippingOptionId = 1L;
    when(shippingOptionRepository.findById(shippingOptionId))
        .thenReturn(Optional.of(shippingOption));
    when(shippingOptionMapper.toDto(shippingOption)).thenReturn(shippingOptionDTO);

    // Act
    ShippingOptionDTO result = shippingOptionService.getShippingOption(shippingOptionId);

    // Assert
    assertNotNull(result);
    assertEquals(shippingOptionDTO, result);
    verify(shippingOptionRepository, times(1)).findById(shippingOptionId);
    verify(shippingOptionMapper, times(1)).toDto(shippingOption);
  }

  @Test
  void getShippingOption_NonExistingId_ThrowsException() {
    // Arrange
    Long shippingOptionId = 999L;
    when(shippingOptionRepository.findById(shippingOptionId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ShippingOptionNotFoundException.class,
        () -> {
          shippingOptionService.getShippingOption(shippingOptionId);
        });
    verify(shippingOptionRepository, times(1)).findById(shippingOptionId);
    verify(shippingOptionMapper, never()).toDto(any(ShippingOption.class));
  }

  @Test
  void createShippingOption_ValidData_ReturnsCreatedShippingOption() {
    // Arrange
    when(shippingOptionMapper.toEntity(createShippingOptionDTO)).thenReturn(shippingOption);
    when(shippingOptionRepository.save(any(ShippingOption.class))).thenReturn(shippingOption);
    when(shippingOptionMapper.toDto(shippingOption)).thenReturn(shippingOptionDTO);

    // Act
    ShippingOptionDTO result = shippingOptionService.createShippingOption(createShippingOptionDTO);

    // Assert
    assertNotNull(result);
    assertEquals(shippingOptionDTO, result);
    verify(shippingOptionMapper, times(1)).toEntity(createShippingOptionDTO);
    verify(shippingOptionRepository, times(1)).save(any(ShippingOption.class));
    verify(shippingOptionMapper, times(1)).toDto(shippingOption);
  }

  @Test
  void createShippingOption_EmptyName_ThrowsException() {
    // Arrange
    CreateShippingOptionDTO invalidDTO = new CreateShippingOptionDTO();
    invalidDTO.setName("");
    invalidDTO.setPrice(15.0);
    invalidDTO.setEstimatedDays(2);

    // Act & Assert
    assertThrows(
        ShippingOptionValidationException.class,
        () -> {
          shippingOptionService.createShippingOption(invalidDTO);
        });
    verify(shippingOptionRepository, never()).save(any(ShippingOption.class));
  }

  @Test
  void createShippingOption_NegativePrice_ThrowsException() {
    // Arrange
    CreateShippingOptionDTO invalidDTO = new CreateShippingOptionDTO();
    invalidDTO.setName("Test Shipping");
    invalidDTO.setPrice(-5.0);
    invalidDTO.setEstimatedDays(2);

    // Act & Assert
    assertThrows(
        ShippingOptionValidationException.class,
        () -> {
          shippingOptionService.createShippingOption(invalidDTO);
        });
    verify(shippingOptionRepository, never()).save(any(ShippingOption.class));
  }

  @Test
  void createShippingOption_NegativeDays_ThrowsException() {
    // Arrange
    CreateShippingOptionDTO invalidDTO = new CreateShippingOptionDTO();
    invalidDTO.setName("Test Shipping");
    invalidDTO.setPrice(15.0);
    invalidDTO.setEstimatedDays(-1);

    // Act & Assert
    assertThrows(
        ShippingOptionValidationException.class,
        () -> {
          shippingOptionService.createShippingOption(invalidDTO);
        });
    verify(shippingOptionRepository, never()).save(any(ShippingOption.class));
  }

  @Test
  void updateShippingOption_ExistingId_ReturnsUpdatedShippingOption() {
    // Arrange
    Long shippingOptionId = 1L;
    CreateShippingOptionDTO updateDTO = new CreateShippingOptionDTO();
    updateDTO.setName("Updated Shipping");
    updateDTO.setPrice(20.0);
    updateDTO.setEstimatedDays(3);
    updateDTO.setDescription("Updated description");

    when(shippingOptionRepository.findById(shippingOptionId))
        .thenReturn(Optional.of(shippingOption));
    when(shippingOptionRepository.save(any(ShippingOption.class))).thenReturn(shippingOption);
    when(shippingOptionMapper.toDto(shippingOption)).thenReturn(shippingOptionDTO);
    doNothing().when(shippingOptionMapper).updateShippingOptionFromDto(updateDTO, shippingOption);

    // Act
    ShippingOptionDTO result =
        shippingOptionService.updateShippingOption(shippingOptionId, updateDTO);

    // Assert
    assertNotNull(result);
    assertEquals(shippingOptionDTO, result);
    verify(shippingOptionRepository, times(1)).findById(shippingOptionId);
    verify(shippingOptionMapper, times(1)).updateShippingOptionFromDto(updateDTO, shippingOption);
    verify(shippingOptionRepository, times(1)).save(shippingOption);
    verify(shippingOptionMapper, times(1)).toDto(shippingOption);
  }

  @Test
  void updateShippingOption_NonExistingId_ThrowsException() {
    // Arrange
    Long shippingOptionId = 999L;
    CreateShippingOptionDTO updateDTO = new CreateShippingOptionDTO();
    updateDTO.setName("Updated Shipping");
    updateDTO.setPrice(20.0);
    updateDTO.setEstimatedDays(3);

    when(shippingOptionRepository.findById(shippingOptionId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ShippingOptionNotFoundException.class,
        () -> {
          shippingOptionService.updateShippingOption(shippingOptionId, updateDTO);
        });
    verify(shippingOptionRepository, times(1)).findById(shippingOptionId);
    verify(shippingOptionRepository, never()).save(any(ShippingOption.class));
  }

  @Test
  void updateShippingOption_InvalidData_ThrowsException() {
    // Arrange
    Long shippingOptionId = 1L;
    CreateShippingOptionDTO updateDTO = new CreateShippingOptionDTO();
    updateDTO.setName("");
    updateDTO.setPrice(20.0);
    updateDTO.setEstimatedDays(3);

    when(shippingOptionRepository.findById(shippingOptionId))
        .thenReturn(Optional.of(shippingOption));

    // Act & Assert
    assertThrows(
        ShippingOptionValidationException.class,
        () -> {
          shippingOptionService.updateShippingOption(shippingOptionId, updateDTO);
        });
    verify(shippingOptionRepository, times(1)).findById(shippingOptionId);
    verify(shippingOptionRepository, never()).save(any(ShippingOption.class));
  }

  @Test
  void deleteShippingOption_ExistingId_DeletesShippingOption() {
    // Arrange
    Long shippingOptionId = 1L;
    when(shippingOptionRepository.existsById(shippingOptionId)).thenReturn(true);
    doNothing().when(shippingOptionRepository).deleteById(shippingOptionId);

    // Act
    shippingOptionService.deleteShippingOption(shippingOptionId);

    // Assert
    verify(shippingOptionRepository, times(1)).existsById(shippingOptionId);
    verify(shippingOptionRepository, times(1)).deleteById(shippingOptionId);
  }

  @Test
  void deleteShippingOption_NonExistingId_ThrowsException() {
    // Arrange
    Long shippingOptionId = 999L;
    when(shippingOptionRepository.existsById(shippingOptionId)).thenReturn(false);

    // Act & Assert
    assertThrows(
        ShippingOptionNotFoundException.class,
        () -> {
          shippingOptionService.deleteShippingOption(shippingOptionId);
        });
    verify(shippingOptionRepository, times(1)).existsById(shippingOptionId);
    verify(shippingOptionRepository, never()).deleteById(shippingOptionId);
  }
}
