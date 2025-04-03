package stud.ntnu.no.backend.favorite.mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class FavoriteMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public FavoriteDTO toDTO(Favorite favorite) {
        return new FavoriteDTO(
            favorite.getId(),
            favorite.getUserId(),  // Using getUserId() which returns String
            favorite.getItemId(),  // Using getItemId() which returns Long
            favorite.getCreatedAt()
        );
    }

    public Favorite toEntity(CreateFavoriteRequest request) {
        // Convert String userId to Long for repository lookup
        Long userIdLong = Long.parseLong(request.getUserId());

        User user = userRepository.findById(userIdLong)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(request.getItemId())
            .orElseThrow(() -> new RuntimeException("Item not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItem(item);
        favorite.setCreatedAt(java.time.LocalDateTime.now());
        // Bruker getIsActive() med null-sjekk: hvis null, så setter vi false
        favorite.setActive(request.getIsActive() != null ? request.getIsActive() : false);
        return favorite;
    }

    public void updateEntity(Favorite favorite, CreateFavoriteRequest request) {
        favorite.setActive(request.getIsActive() != null ? request.getIsActive() : false);
    }
}
