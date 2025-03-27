package stud.ntnu.no.backend.Favorite.Service;

import stud.ntnu.no.backend.Favorite.DTOs.CreateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.DTOs.FavoriteDTO;
import stud.ntnu.no.backend.Favorite.DTOs.UpdateFavoriteRequest;

import java.util.List;

public interface FavoriteService {
    List<FavoriteDTO> getAllFavorites();
    List<FavoriteDTO> getFavoritesByUserId(String userId);
    FavoriteDTO getFavoriteById(Long id);
    FavoriteDTO createFavorite(CreateFavoriteRequest request);
    FavoriteDTO updateFavorite(Long id, UpdateFavoriteRequest request);
    void deleteFavorite(Long id);
}