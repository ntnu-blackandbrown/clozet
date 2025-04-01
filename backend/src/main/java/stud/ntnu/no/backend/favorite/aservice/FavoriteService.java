package stud.ntnu.no.backend.favorite.aservice;

import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.dto.UpdateFavoriteRequest;

import java.util.List;

public interface FavoriteService {
    List<FavoriteDTO> getAllFavorites();
    List<FavoriteDTO> getFavoritesByUserId(String userId);
    FavoriteDTO getFavoriteById(Long id);
    FavoriteDTO createFavorite(CreateFavoriteRequest request);
    FavoriteDTO updateFavorite(Long id, UpdateFavoriteRequest request);
    void deleteFavorite(Long id);
}