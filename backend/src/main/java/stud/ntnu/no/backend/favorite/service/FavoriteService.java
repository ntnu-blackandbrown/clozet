package stud.ntnu.no.backend.favorite.service;

import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.exception.FavoriteNotFoundException;
import stud.ntnu.no.backend.favorite.exception.FavoriteValidationException;

import java.util.List;

/**
 * Service-grensesnitt for håndtering av favoritter.
 * Definerer operasjoner for henting, opprettelse, oppdatering og sletting av favoritter.
 */
public interface FavoriteService {

    /**
     * Henter alle favoritter i systemet.
     *
     * @return En liste med alle favoritter som FavoriteDTO-objekter
     */
    List<FavoriteDTO> getAllFavorites();

    /**
     * Henter alle favoritter for en spesifikk bruker.
     *
     * @param userId Brukerens ID
     * @return En liste med brukerens favoritter som FavoriteDTO-objekter
     * @throws FavoriteValidationException hvis bruker-ID er null eller tom
     */
    List<FavoriteDTO> getFavoritesByUserId(String userId);

    /**
     * Henter en spesifikk favoritt basert på ID.
     *
     * @param id Favorittens ID
     * @return FavoriteDTO-objekt for den spesifikke favoritten
     * @throws FavoriteValidationException hvis ID er null
     * @throws FavoriteNotFoundException hvis favoritten ikke finnes
     */
    FavoriteDTO getFavoriteById(Long id);

    /**
     * Oppretter en ny favoritt.
     *
     * @param request Forespørsel med data for den nye favoritten
     * @return FavoriteDTO-objekt for den nye favoritten
     * @throws FavoriteValidationException hvis forespørselen er ugyldig eller favoritten allerede eksisterer
     */
    FavoriteDTO createFavorite(CreateFavoriteRequest request);

    /**
     * Oppdaterer en eksisterende favoritt.
     *
     * @param id Favorittens ID
     * @param request Forespørsel med oppdaterte data
     * @return FavoriteDTO-objekt for den oppdaterte favoritten
     * @throws FavoriteValidationException hvis ID eller forespørselen er ugyldig
     * @throws FavoriteNotFoundException hvis favoritten ikke finnes
     */
    FavoriteDTO updateFavorite(Long id, CreateFavoriteRequest request);

    /**
     * Sletter en favoritt.
     *
     * @param id Favorittens ID
     * @throws FavoriteValidationException hvis ID er null
     * @throws FavoriteNotFoundException hvis favoritten ikke finnes
     */
    void deleteFavorite(Long id);
}