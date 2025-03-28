package stud.ntnu.no.backend.favorite.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(Long id) {
        super("Favorite not found with id: " + id);
    }
}