package stud.ntnu.no.backend.shippingoption.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.shippingoption.exception.ShippingOptionNotFoundException;
import stud.ntnu.no.backend.shippingoption.exception.ShippingOptionValidationException;
import stud.ntnu.no.backend.shippingoption.mapper.ShippingOptionMapper;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;

/**
 * Implementation of the ShippingOptionService interface.
 * <p>
 * This class provides methods for managing shipping options, including CRUD operations.
 */
@Service
public class ShippingOptionServiceImpl implements ShippingOptionService {

  private static final Logger logger = LoggerFactory.getLogger(ShippingOptionServiceImpl.class);

  private final ShippingOptionRepository shippingOptionRepository;
  private final ShippingOptionMapper shippingOptionMapper;

  /**
   * Constructs a new ShippingOptionServiceImpl with the specified dependencies.
   *
   * @param shippingOptionRepository the ShippingOptionRepository
   * @param shippingOptionMapper     the ShippingOptionMapper
   */
  public ShippingOptionServiceImpl(ShippingOptionRepository shippingOptionRepository,
      ShippingOptionMapper shippingOptionMapper) {
    this.shippingOptionRepository = shippingOptionRepository;
    this.shippingOptionMapper = shippingOptionMapper;
  }

  @Override
  public List<ShippingOptionDTO> getAllShippingOptions() {
    logger.info("Fetching all shipping options");
    return shippingOptionMapper.toDtoList(shippingOptionRepository.findAll());
  }

  @Override
  public ShippingOptionDTO getShippingOption(Long id) {
    logger.info("Fetching shipping option with id: {}", id);
    ShippingOption shippingOption = shippingOptionRepository.findById(id)
        .orElseThrow(() -> new ShippingOptionNotFoundException(id));
    return shippingOptionMapper.toDto(shippingOption);
  }

  @Override
  @Transactional
  public ShippingOptionDTO createShippingOption(CreateShippingOptionDTO shippingOptionDTO) {
    logger.info("Creating new shipping option with name: {}", shippingOptionDTO.getName());
    validateShippingOption(shippingOptionDTO);
    ShippingOption shippingOption = shippingOptionMapper.toEntity(shippingOptionDTO);
    shippingOption = shippingOptionRepository.save(shippingOption);
    return shippingOptionMapper.toDto(shippingOption);
  }

  @Override
  @Transactional
  public ShippingOptionDTO updateShippingOption(Long id,
      CreateShippingOptionDTO shippingOptionDTO) {
    logger.info("Updating shipping option with id: {}", id);
    ShippingOption shippingOption = shippingOptionRepository.findById(id)
        .orElseThrow(() -> new ShippingOptionNotFoundException(id));

    validateShippingOption(shippingOptionDTO);
    shippingOptionMapper.updateShippingOptionFromDto(shippingOptionDTO, shippingOption);
    shippingOption = shippingOptionRepository.save(shippingOption);
    return shippingOptionMapper.toDto(shippingOption);
  }

  @Override
  @Transactional
  public void deleteShippingOption(Long id) {
    logger.info("Deleting shipping option with id: {}", id);
    if (!shippingOptionRepository.existsById(id)) {
      throw new ShippingOptionNotFoundException(id);
    }
    shippingOptionRepository.deleteById(id);
  }

  /**
   * Validates the given CreateShippingOptionDTO.
   * <p>
   * This method checks that the name is not null or empty, the price is non-negative, and the
   * estimated days are non-negative.
   *
   * @param shippingOptionDTO the CreateShippingOptionDTO to validate
   * @throws ShippingOptionValidationException if validation fails
   */
  private void validateShippingOption(CreateShippingOptionDTO shippingOptionDTO) {
    if (shippingOptionDTO.getName() == null || shippingOptionDTO.getName().trim().isEmpty()) {
      throw new ShippingOptionValidationException("Name cannot be empty");
    }

    if (shippingOptionDTO.getPrice() < 0) {
      throw new ShippingOptionValidationException("Price cannot be negative");
    }

    if (shippingOptionDTO.getEstimatedDays() < 0) {
      throw new ShippingOptionValidationException("Estimated days cannot be negative");
    }
  }
}
