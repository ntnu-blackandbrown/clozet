package stud.ntnu.no.backend.favorite.service;

import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;

import java.util.List;

public interface FavoriteService {
    List<FavoriteDTO> getAllFavorites();
    List<FavoriteDTO> getFavoritesByUserId(String userId);
    FavoriteDTO getFavoriteById(Long id);
    FavoriteDTO createFavorite(CreateFavoriteRequest request);

    FavoriteDTO updateFavorite(Long id, CreateFavoriteRequest request);

    void deleteFavorite(Long id);
}