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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the FavoriteService interface that handles favorite functionality.
 * This service manages operations for retrieving, creating, updating, and deleting favorites.
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    @Autowired
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
    }

    /**
     * Retrieves all favorites in the system.
     *
     * @return A list of all favorites as FavoriteDTO objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getAllFavorites() {
        logger.info("Retrieving all favorites.");
        List<FavoriteDTO> favorites = favoriteRepository.findAll().stream()
            .map(favoriteMapper::toDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} favorites.", favorites.size());
        return favorites;
    }

    /**
     * Retrieves all favorites for a specific user.
     *
     * @param userId The ID of the user
     * @return A list of the user's favorites as FavoriteDTO objects
     * @throws FavoriteValidationException if the user ID is null or empty
     */
    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getFavoritesByUserId(String userId) {
        logger.info("Retrieving favorites for user ID: {}", userId);
        if (userId == null || userId.trim().isEmpty()) {
            logger.warn("User ID is null or empty.");
            throw new FavoriteValidationException("User ID cannot be null or empty");
        }
        
        // Validate that userId is numeric
        try {
            Long.parseLong(userId);
        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", userId);
            throw new FavoriteValidationException("Invalid user ID format");
        }
        
        List<FavoriteDTO> favorites = favoriteRepository.findByUserId(userId).stream()
            .map(favoriteMapper::toDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} favorites for user ID: {}", favorites.size(), userId);
        return favorites;
    }

    /**
     * Retrieves a specific favorite by ID.
     *
     * @param id The ID of the favorite
     * @return FavoriteDTO object for the specific favorite
     * @throws FavoriteValidationException if the ID is null
     * @throws FavoriteNotFoundException if the favorite is not found
     */
    @Override
    @Transactional(readOnly = true)
    public FavoriteDTO getFavoriteById(Long id) {
        logger.info("Retrieving favorite by ID: {}", id);
        if (id == null) {
            logger.warn("Favorite ID is null.");
            throw new FavoriteValidationException("Favorite ID cannot be null");
        }
        
        Favorite favorite = favoriteRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Favorite not found with ID: {}", id);
                return new FavoriteNotFoundException(id);
            });
        FavoriteDTO favoriteDTO = favoriteMapper.toDTO(favorite);
        logger.info("Retrieved favorite: {}", favoriteDTO);
        return favoriteDTO;
    }

    /**
     * Creates a new favorite.
     *
     * @param request Request with data for the new favorite
     * @return FavoriteDTO object for the new favorite
     * @throws FavoriteValidationException if the request is invalid or the favorite already exists
     */
    @Override
    @Transactional
    public FavoriteDTO createFavorite(CreateFavoriteRequest request) {
        logger.info("Creating a new favorite with request: {}", request);
        validateCreateRequest(request);
        
        // Sjekk om favoritt for denne brukeren og dette elementet allerede eksisterer
        if (favoriteRepository.existsByUserIdAndItemId(request.getUserId(), request.getItemId())) {
            logger.warn("Favorite already exists for user ID {} and item ID {}", request.getUserId(), request.getItemId());
            throw new FavoriteValidationException("Favorite already exists for this user and item");
        }
        
        Favorite favorite = favoriteMapper.toEntity(request);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        FavoriteDTO favoriteDTO = favoriteMapper.toDTO(savedFavorite);
        logger.info("Created favorite: {}", favoriteDTO);
        return favoriteDTO;
    }

    /**
     * Updates an existing favorite.
     *
     * @param id The ID of the favorite
     * @param request Request with updated data
     * @return FavoriteDTO object for the updated favorite
     * @throws FavoriteValidationException if the ID or request is invalid
     * @throws FavoriteNotFoundException if the favorite is not found
     */
    @Override
    @Transactional
    public FavoriteDTO updateFavorite(Long id, CreateFavoriteRequest request) {
        logger.info("Updating favorite with ID: {}, request: {}", id, request);
        if (id == null) {
            logger.warn("Favorite ID is null.");
            throw new FavoriteValidationException("Favorite ID cannot be null");
        }
        
        validateCreateRequest(request);
        
        Favorite favorite = favoriteRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Favorite not found with ID: {}", id);
                return new FavoriteNotFoundException(id);
            });
        favoriteMapper.updateEntity(favorite, request);
        Favorite updatedFavorite = favoriteRepository.save(favorite);
        FavoriteDTO favoriteDTO = favoriteMapper.toDTO(updatedFavorite);
        logger.info("Updated favorite: {}", favoriteDTO);
        return favoriteDTO;
    }

    /**
     * Deletes a favorite.
     *
     * @param id The ID of the favorite
     * @throws FavoriteValidationException if the ID is null
     * @throws FavoriteNotFoundException if the favorite is not found
     * Sletter en favoritt.
     * 
     * @param id Favorittens ID
     * @throws FavoriteValidationException hvis ID er null
     * @throws FavoriteNotFoundException hvis favoritten ikke finnes
     */
    @Override
    @Transactional
    public void deleteFavorite(Long id) {
        logger.info("Deleting favorite with ID: {}", id);
        if (id == null) {
            logger.warn("Favorite ID is null.");
            throw new FavoriteValidationException("Favorite ID cannot be null");
        }
        
        if (!favoriteRepository.existsById(id)) {
            logger.warn("Favorite not found with ID: {}", id);
            throw new FavoriteNotFoundException(id);
        }
        favoriteRepository.deleteById(id);
        logger.info("Deleted favorite with ID: {}", id);
    }
    
    /**
     * Validerer en favoritt-forespørsel.
     * 
     * @param request Forespørselen som skal valideres
     * @throws FavoriteValidationException hvis forespørselen er ugyldig
     */
    private void validateCreateRequest(CreateFavoriteRequest request) {
        logger.info("Validating create request: {}", request);
        if (request == null) {
            logger.warn("Request is null.");
            throw new FavoriteValidationException("Request cannot be null");
        }
        
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            logger.warn("User ID is null or empty.");
            throw new FavoriteValidationException("User ID cannot be null or empty");
        }
        
        if (request.getItemId() == null) {
            logger.warn("Item ID is null.");
            throw new FavoriteValidationException("Item ID cannot be null");
        }
    }
}
