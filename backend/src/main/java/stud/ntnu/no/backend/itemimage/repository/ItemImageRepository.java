package stud.ntnu.no.backend.itemimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;

import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    List<ItemImage> findByItemId(Long itemId);
    List<ItemImage> findByItemIdOrderByDisplayOrder(Long itemId);
}