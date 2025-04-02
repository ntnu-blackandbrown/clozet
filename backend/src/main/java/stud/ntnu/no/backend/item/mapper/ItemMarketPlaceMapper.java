package stud.ntnu.no.backend.item.mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.item.dto.ItemMarketPlaceDTO;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.user.entity.User;

import java.util.Comparator;

@Component
public class ItemMarketPlaceMapper {

    public ItemMarketPlaceDTO toItemMarketPlaceDTO(Item item, User currentUser) {
        // Find the primary image or first by display order
        String imageUrl = item.getImages().stream()
                .sorted(Comparator.comparing(ItemImage::isPrimary).reversed()
                        .thenComparing(ItemImage::getDisplayOrder))
                .findFirst()
                .map(ItemImage::getImageUrl)
                .orElse(null);

        // Check if item is wishlisted by current user
        boolean isWishlisted = false;
        if (currentUser != null) {
            isWishlisted = item.getFavorites().stream()
                    .anyMatch(favorite -> favorite.getUser().getId().equals(currentUser.getId()));
        }

        return new ItemMarketPlaceDTO(
                item.getId(),
                item.getTitle(),
                item.getPrice(),
                item.getCategory().getName(),
                imageUrl,
                item.getLocation().getCity(),
                item.isVippsPaymentEnabled(),
                isWishlisted
        );
    }
}