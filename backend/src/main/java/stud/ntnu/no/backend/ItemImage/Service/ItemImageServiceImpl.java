package stud.ntnu.no.backend.ItemImage.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.ItemImage.DTOs.CreateItemImageDTO;
import stud.ntnu.no.backend.ItemImage.DTOs.ItemImageDTO;
import stud.ntnu.no.backend.ItemImage.Entity.ItemImage;
import stud.ntnu.no.backend.ItemImage.Exceptions.ItemImageNotFoundException;
import stud.ntnu.no.backend.ItemImage.Exceptions.ItemImageValidationException;
import stud.ntnu.no.backend.ItemImage.Mapper.ItemImageMapper;
import stud.ntnu.no.backend.ItemImage.Repository.ItemImageRepository;

import java.util.List;

@Service
public class ItemImageServiceImpl implements ItemImageService {

    private final ItemImageRepository itemImageRepository;
    private final ItemImageMapper itemImageMapper;

    public ItemImageServiceImpl(ItemImageRepository itemImageRepository, ItemImageMapper itemImageMapper) {
        this.itemImageRepository = itemImageRepository;
        this.itemImageMapper = itemImageMapper;
    }

    @Override
    public List<ItemImageDTO> getAllItemImages() {
        return itemImageMapper.toDtoList(itemImageRepository.findAll());
    }

    @Override
    public List<ItemImageDTO> getItemImagesByItemId(Long itemId) {
        return itemImageMapper.toDtoList(itemImageRepository.findByItemId(itemId));
    }

    @Override
    public ItemImageDTO getItemImage(Long id) {
        ItemImage itemImage = itemImageRepository.findById(id)
                .orElseThrow(() -> new ItemImageNotFoundException(id));
        return itemImageMapper.toDto(itemImage);
    }

    @Override
    @Transactional
    public ItemImageDTO createItemImage(CreateItemImageDTO itemImageDTO) {
        validateItemImage(itemImageDTO);
        ItemImage itemImage = itemImageMapper.toEntity(itemImageDTO);
        itemImage = itemImageRepository.save(itemImage);
        return itemImageMapper.toDto(itemImage);
    }

    @Override
    @Transactional
    public ItemImageDTO updateItemImage(Long id, CreateItemImageDTO itemImageDTO) {
        ItemImage itemImage = itemImageRepository.findById(id)
                .orElseThrow(() -> new ItemImageNotFoundException(id));

        validateItemImage(itemImageDTO);
        itemImageMapper.updateItemImageFromDto(itemImageDTO, itemImage);
        itemImage = itemImageRepository.save(itemImage);
        return itemImageMapper.toDto(itemImage);
    }

    @Override
    @Transactional
    public void deleteItemImage(Long id) {
        if (!itemImageRepository.existsById(id)) {
            throw new ItemImageNotFoundException(id);
        }
        itemImageRepository.deleteById(id);
    }

    private void validateItemImage(CreateItemImageDTO itemImageDTO) {
        if (itemImageDTO.getImageUrl() == null || itemImageDTO.getImageUrl().trim().isEmpty()) {
            throw new ItemImageValidationException("Image URL cannot be empty");
        }
    }
}