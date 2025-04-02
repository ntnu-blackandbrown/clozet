package stud.ntnu.no.backend.favorite.mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.dto.UpdateFavoriteRequest;
import stud.ntnu.no.backend.favorite.entity.Favorite;

@Component
public class FavoriteMapper {
    public FavoriteDTO toDTO(Favorite favorite) {
        return new FavoriteDTO(
                favorite.getId(),
                favorite.getUserId(),
                favorite.getItemId(),
                favorite.getCreatedAt()
        );
    }

    public Favorite toEntity(CreateFavoriteRequest request) {
        return new Favorite(
                request.getUserId(),
                request.getItemId()
        );
    }


}