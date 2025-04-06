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
 * Mapper class for converting between Favorite entity and DTO objects.
 */
@Mapper(componentModel = "spring")
public abstract class FavoriteMapper {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ItemRepository itemRepository;

    /**
     * Maps a Favorite entity to a FavoriteDTO.
     *
     * @param favorite The Favorite entity to be mapped
     * @return A FavoriteDTO object with data from the entity
     */
    @Mapping(target = "userId", expression = "java(favorite.getUserId())")
    @Mapping(target = "itemId", expression = "java(favorite.getItemId())")
    @Mapping(target = "updatedAt", expression = "java(favorite.getCreatedAt())")
    public abstract FavoriteDTO toDTO(Favorite favorite);

    /**
     * Maps a list of Favorite entities to a list of FavoriteDTO objects.
     *
     * @param favorites The list of Favorite entities
     * @return A list of FavoriteDTO objects
     */
    public abstract List<FavoriteDTO> toDTOList(List<Favorite> favorites);

    /**
     * Maps a CreateFavoriteRequest to a Favorite entity.
     * Retrieves user and item from respective repositories.
     *
     * @param request The CreateFavoriteRequest object to be mapped
     * @return A Favorite entity
     * @throws RuntimeException if user or item is not found
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
     * Updates an existing Favorite entity based on data from a CreateFavoriteRequest.
     *
     * @param favorite The Favorite entity to be updated
     * @param request CreateFavoriteRequest with updated data
     */
    public void updateEntity(Favorite favorite, CreateFavoriteRequest request) {
        if (request == null) {
            return;
        }
        favorite.setActive(request.getActive());
    }
}
