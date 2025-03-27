package stud.ntnu.no.backend.ShippingOption.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.ShippingOption.DTOs.CreateShippingOptionDTO;
import stud.ntnu.no.backend.ShippingOption.DTOs.ShippingOptionDTO;
import stud.ntnu.no.backend.ShippingOption.Entity.ShippingOption;
import stud.ntnu.no.backend.ShippingOption.Exceptions.ShippingOptionNotFoundException;
import stud.ntnu.no.backend.ShippingOption.Exceptions.ShippingOptionValidationException;
import stud.ntnu.no.backend.ShippingOption.Mapper.ShippingOptionMapper;
import stud.ntnu.no.backend.ShippingOption.repository.ShippingOptionRepository;


import java.util.List;

@Service
public class ShippingOptionServiceImpl implements ShippingOptionService {

    private final ShippingOptionRepository shippingOptionRepository;
    private final ShippingOptionMapper shippingOptionMapper;

    public ShippingOptionServiceImpl(ShippingOptionRepository shippingOptionRepository, 
                                     ShippingOptionMapper shippingOptionMapper) {
        this.shippingOptionRepository = shippingOptionRepository;
        this.shippingOptionMapper = shippingOptionMapper;
    }

    @Override
    public List<ShippingOptionDTO> getAllShippingOptions() {
        return shippingOptionMapper.toDtoList(shippingOptionRepository.findAll());
    }

    @Override
    public ShippingOptionDTO getShippingOption(Long id) {
        ShippingOption shippingOption = shippingOptionRepository.findById(id)
                .orElseThrow(() -> new ShippingOptionNotFoundException(id));
        return shippingOptionMapper.toDto(shippingOption);
    }

    @Override
    @Transactional
    public ShippingOptionDTO createShippingOption(CreateShippingOptionDTO shippingOptionDTO) {
        validateShippingOption(shippingOptionDTO);
        ShippingOption shippingOption = shippingOptionMapper.toEntity(shippingOptionDTO);
        shippingOption = shippingOptionRepository.save(shippingOption);
        return shippingOptionMapper.toDto(shippingOption);
    }

    @Override
    @Transactional
    public ShippingOptionDTO updateShippingOption(Long id, CreateShippingOptionDTO shippingOptionDTO) {
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
        if (!shippingOptionRepository.existsById(id)) {
            throw new ShippingOptionNotFoundException(id);
        }
        shippingOptionRepository.deleteById(id);
    }

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