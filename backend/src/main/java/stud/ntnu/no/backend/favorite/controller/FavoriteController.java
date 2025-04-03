package stud.ntnu.no.backend.favorite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.service.FavoriteService;

import java.util.List;

/**
 * REST-kontroller for å håndtere favoritt-operasjoner.
 * Eksponerer endepunkter for å hente, opprette, oppdatere og slette favoritter.
 */
@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Henter alle favoritter.
     *
     * @return Liste med alle favoritter
     */
    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getAllFavorites() {
        List<FavoriteDTO> favorites = favoriteService.getAllFavorites();
        return ResponseEntity.ok(favorites);
    }

    /**
     * Henter en spesifikk favoritt basert på ID.
     *
     * @param id Favorittens ID
     * @return Favoritten med angitt ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FavoriteDTO> getFavoriteById(@PathVariable Long id) {
        FavoriteDTO favorite = favoriteService.getFavoriteById(id);
        return ResponseEntity.ok(favorite);
    }

    /**
     * Henter alle favoritter for en spesifikk bruker.
     *
     * @param userId Brukerens ID
     * @return Liste med brukerens favoritter
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUserId(@PathVariable String userId) {
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }

    /**
     * Oppretter en ny favoritt.
     *
     * @param request Data for å opprette en ny favoritt
     * @return Den nyopprettede favoritten
     */
    @PostMapping
    public ResponseEntity<FavoriteDTO> createFavorite(@RequestBody CreateFavoriteRequest request) {
        FavoriteDTO createdFavorite = favoriteService.createFavorite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFavorite);
    }

    /**
     * Oppdaterer en eksisterende favoritt.
     *
     * @param id Favorittens ID
     * @param request Data for å oppdatere favoritten
     * @return Den oppdaterte favoritten
     */
    @PutMapping("/{id}")
    public ResponseEntity<FavoriteDTO> updateFavorite(@PathVariable Long id, @RequestBody CreateFavoriteRequest request) {
        FavoriteDTO updatedFavorite = favoriteService.updateFavorite(id, request);
        return ResponseEntity.ok(updatedFavorite);
    }

    /**
     * Sletter en favoritt.
     *
     * @param id Favorittens ID
     * @return Tomt svar med statusen NO_CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}