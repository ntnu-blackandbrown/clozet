package stud.ntnu.no.backend.Favorite.Mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.Favorite.DTOs.CreateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.DTOs.FavoriteDTO;
import stud.ntnu.no.backend.Favorite.DTOs.UpdateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.Entity.Favorite;

@Component
public class FavoriteMapper {
    public FavoriteDTO toDTO(Favorite favorite) {
        return new FavoriteDTO(
                favorite.getId(),
                favorite.getUserId(),
                favorite.getItemId(),
                favorite.getItemType(),
                favorite.getCreatedAt()
        );
    }

    public Favorite toEntity(CreateFavoriteRequest request) {
        return new Favorite(
                request.getUserId(),
                request.getItemId(),
                request.getItemType()
        );
    }

    public void updateEntityFromRequest(Favorite favorite, UpdateFavoriteRequest request) {
        if (request.getItemType() != null) {
            favorite.setItemType(request.getItemType());
        }
    }
}