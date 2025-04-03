package stud.ntnu.no.backend.favorite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.favorite.exception.FavoriteNotFoundException;
import stud.ntnu.no.backend.favorite.exception.FavoriteValidationException;
import stud.ntnu.no.backend.favorite.mapper.FavoriteMapper;
import stud.ntnu.no.backend.favorite.repository.FavoriteRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementasjon av FavoriteService-grensesnittet som håndterer favoritt-funksjonalitet.
 * Denne tjenesten håndterer operasjoner for å hente, opprette, oppdatere og slette favoritter.
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    @Autowired
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
    }

    /**
     * Henter alle favoritter i systemet.
     * 
     * @return En liste med alle favoritter som FavoriteDTO-objekter
     */
    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getAllFavorites() {
        return favoriteRepository.findAll().stream()
            .map(favoriteMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Henter alle favoritter for en spesifikk bruker.
     * 
     * @param userId Brukerens ID
     * @return En liste med brukerens favoritter som FavoriteDTO-objekter
     * @throws FavoriteValidationException hvis bruker-ID er null eller tom
     */
    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getFavoritesByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new FavoriteValidationException("User ID cannot be null or empty");
        }
        
        return favoriteRepository.findByUserId(userId).stream()
            .map(favoriteMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Henter en spesifikk favoritt basert på ID.
     * 
     * @param id Favorittens ID
     * @return FavoriteDTO-objekt for den spesifikke favoritten
     * @throws FavoriteValidationException hvis ID er null
     * @throws FavoriteNotFoundException hvis favoritten ikke finnes
     */
    @Override
    @Transactional(readOnly = true)
    public FavoriteDTO getFavoriteById(Long id) {
        if (id == null) {
            throw new FavoriteValidationException("Favorite ID cannot be null");
        }
        
        Favorite favorite = favoriteRepository.findById(id)
            .orElseThrow(() -> new FavoriteNotFoundException(id));
        return favoriteMapper.toDTO(favorite);
    }

    /**
     * Oppretter en ny favoritt.
     * 
     * @param request Forespørsel med data for den nye favoritten
     * @return FavoriteDTO-objekt for den nye favoritten
     * @throws FavoriteValidationException hvis forespørselen er ugyldig eller favoritten allerede eksisterer
     */
    @Override
    @Transactional
    public FavoriteDTO createFavorite(CreateFavoriteRequest request) {
        validateCreateRequest(request);
        
        // Sjekk om favoritt for denne brukeren og dette elementet allerede eksisterer
        if (favoriteRepository.existsByUserIdAndItemId(request.getUserId(), request.getItemId())) {
            throw new FavoriteValidationException("Favorite already exists for this user and item");
        }
        
        Favorite favorite = favoriteMapper.toEntity(request);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return favoriteMapper.toDTO(savedFavorite);
    }

    /**
     * Oppdaterer en eksisterende favoritt.
     * 
     * @param id Favorittens ID
     * @param request Forespørsel med oppdaterte data
     * @return FavoriteDTO-objekt for den oppdaterte favoritten
     * @throws FavoriteValidationException hvis ID eller forespørselen er ugyldig
     * @throws FavoriteNotFoundException hvis favoritten ikke finnes
     */
    @Override
    @Transactional
    public FavoriteDTO updateFavorite(Long id, CreateFavoriteRequest request) {
        if (id == null) {
            throw new FavoriteValidationException("Favorite ID cannot be null");
        }
        
        validateCreateRequest(request);
        
        Favorite favorite = favoriteRepository.findById(id)
            .orElseThrow(() -> new FavoriteNotFoundException(id));
        favoriteMapper.updateEntity(favorite, request);
        Favorite updatedFavorite = favoriteRepository.save(favorite);
        return favoriteMapper.toDTO(updatedFavorite);
    }

    /**
     * Sletter en favoritt.
     * 
     * @param id Favorittens ID
     * @throws FavoriteValidationException hvis ID er null
     * @throws FavoriteNotFoundException hvis favoritten ikke finnes
     */
    @Override
    @Transactional
    public void deleteFavorite(Long id) {
        if (id == null) {
            throw new FavoriteValidationException("Favorite ID cannot be null");
        }
        
        if (!favoriteRepository.existsById(id)) {
            throw new FavoriteNotFoundException(id);
        }
        favoriteRepository.deleteById(id);
    }
    
    /**
     * Validerer en favoritt-forespørsel.
     * 
     * @param request Forespørselen som skal valideres
     * @throws FavoriteValidationException hvis forespørselen er ugyldig
     */
    private void validateCreateRequest(CreateFavoriteRequest request) {
        if (request == null) {
            throw new FavoriteValidationException("Request cannot be null");
        }
        
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new FavoriteValidationException("User ID cannot be null or empty");
        }
        
        if (request.getItemId() == null) {
            throw new FavoriteValidationException("Item ID cannot be null");
        }
    }
}
