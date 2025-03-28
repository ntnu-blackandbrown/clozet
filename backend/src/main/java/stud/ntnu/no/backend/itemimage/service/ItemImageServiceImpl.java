package stud.ntnu.no.backend.itemimage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.itemimage.dto.CreateItemImageDTO;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.itemimage.exception.ItemImageNotFoundException;
import stud.ntnu.no.backend.itemimage.exception.ItemImageValidationException;
import stud.ntnu.no.backend.itemimage.mapper.ItemImageMapper;
import stud.ntnu.no.backend.itemimage.repository.ItemImageRepository;

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