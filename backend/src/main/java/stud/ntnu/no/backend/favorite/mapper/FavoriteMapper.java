package stud.ntnu.no.backend.favorite.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

/**
 * Mapper-klasse for å konvertere mellom Favorite-entitet og DTO-objekter.
 */
@Component
public class FavoriteMapper {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public FavoriteMapper(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Konverterer en Favorite-entitet til en FavoriteDTO.
     *
     * @param favorite Favorite-entiteten som skal konverteres
     * @return Et FavoriteDTO-objekt basert på entiteten
     */
    public FavoriteDTO toDTO(Favorite favorite) {
        return new FavoriteDTO(
            favorite.getId(),
            favorite.getUserId(),
            favorite.getItemId(),
            favorite.isActive(),
            favorite.getCreatedAt(),
            favorite.getCreatedAt() // Bruker createdAt som updatedAt siden det ikke er implementert ennå
        );
    }

    /**
     * Konverterer en CreateFavoriteRequest til en Favorite-entitet.
     *
     * @param request Forespørselen som skal konverteres til entitet
     * @return En Favorite-entitet basert på forespørselen
     * @throws RuntimeException hvis brukeren eller elementet ikke finnes
     */
    public Favorite toEntity(CreateFavoriteRequest request) {
        // Konverter String userId til Long for repository-oppslag
        Long userIdLong = Long.parseLong(request.getUserId());

        User user = userRepository.findById(userIdLong)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(request.getItemId())
            .orElseThrow(() -> new RuntimeException("Item not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItem(item);
        favorite.setCreatedAt(java.time.LocalDateTime.now());
        favorite.setActive(request.getActive());
        return favorite;
    }

    /**
     * Oppdaterer en eksisterende Favorite-entitet basert på en CreateFavoriteRequest.
     *
     * @param favorite Favorite-entiteten som skal oppdateres
     * @param request Forespørselen med nye data
     */
    public void updateEntity(Favorite favorite, CreateFavoriteRequest request) {
        favorite.setActive(request.getActive());
    }
}
