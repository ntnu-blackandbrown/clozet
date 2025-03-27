package stud.ntnu.no.backend.Favorite.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.Favorite.DTOs.CreateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.DTOs.FavoriteDTO;
import stud.ntnu.no.backend.Favorite.DTOs.UpdateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.Entity.Favorite;
import stud.ntnu.no.backend.Favorite.Exceptions.FavoriteNotFoundException;
import stud.ntnu.no.backend.Favorite.Mapper.FavoriteMapper;
import stud.ntnu.no.backend.Favorite.Repository.FavoriteRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    @Autowired
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public List<FavoriteDTO> getAllFavorites() {
        return favoriteRepository.findAll().stream()
                .map(favoriteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FavoriteDTO> getFavoritesByUserId(String userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(favoriteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FavoriteDTO getFavoriteById(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new FavoriteNotFoundException(id));
        return favoriteMapper.toDTO(favorite);
    }

    @Override
    public FavoriteDTO createFavorite(CreateFavoriteRequest request) {
        Favorite favorite = favoriteMapper.toEntity(request);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return favoriteMapper.toDTO(savedFavorite);
    }

    @Override
    public FavoriteDTO updateFavorite(Long id, UpdateFavoriteRequest request) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new FavoriteNotFoundException(id));

        favoriteMapper.updateEntityFromRequest(favorite, request);
        Favorite updatedFavorite = favoriteRepository.save(favorite);
        return favoriteMapper.toDTO(updatedFavorite);
    }

    @Override
    public void deleteFavorite(Long id) {
        if (!favoriteRepository.existsById(id)) {
            throw new FavoriteNotFoundException(id);
        }
        favoriteRepository.deleteById(id);
    }
}