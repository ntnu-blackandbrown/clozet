package stud.ntnu.no.backend.favorite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper-klasse for å konvertere mellom Favorite-entitet og DTO-objekter.
 */
@Mapper(componentModel = "spring")
public abstract class FavoriteMapper {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ItemRepository itemRepository;

    /**
     * Mapper en Favorite-entitet til en FavoriteDTO.
     *
     * @param favorite Favorite-entiteten som skal mappes
     * @return Et FavoriteDTO-objekt med data fra entiteten
     */
    @Mapping(target = "userId", expression = "java(favorite.getUserId())")
    @Mapping(target = "itemId", expression = "java(favorite.getItemId())")
    @Mapping(target = "updatedAt", expression = "java(favorite.getCreatedAt())")
    public abstract FavoriteDTO toDTO(Favorite favorite);

    /**
     * Mapper en liste med Favorite-entiteter til en liste med FavoriteDTO-objekter.
     *
     * @param favorites Listen med Favorite-entiteter
     * @return En liste med FavoriteDTO-objekter
     */
    public abstract List<FavoriteDTO> toDTOList(List<Favorite> favorites);

    /**
     * Mapper en CreateFavoriteRequest til en Favorite-entitet.
     * Henter bruker og item fra respektive repositories.
     *
     * @param request CreateFavoriteRequest-objektet som skal mappes
     * @return En Favorite-entitet
     * @throws RuntimeException hvis bruker eller item ikke finnes
     */
    public Favorite toEntity(CreateFavoriteRequest request) {
        if (request == null) {
            return null;
        }

        // Convert String userId to Long for repository lookup
        Long userIdLong = Long.parseLong(request.getUserId());

        User user = userRepository.findById(userIdLong)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(request.getItemId())
            .orElseThrow(() -> new RuntimeException("Item not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItem(item);
        favorite.setCreatedAt(LocalDateTime.now());
        favorite.setActive(request.getActive());
        return favorite;
    }

    /**
     * Oppdaterer en eksisterende Favorite-entitet basert på data fra en CreateFavoriteRequest.
     *
     * @param favorite Favorite-entiteten som skal oppdateres
     * @param request CreateFavoriteRequest med oppdaterte data
     */
    public void updateEntity(Favorite favorite, CreateFavoriteRequest request) {
        if (request == null) {
            return;
        }
        favorite.setActive(request.getActive());
    }
}
