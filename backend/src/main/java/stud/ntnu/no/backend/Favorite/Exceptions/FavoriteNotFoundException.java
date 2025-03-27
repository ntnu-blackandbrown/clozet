package stud.ntnu.no.backend.Favorite.Exceptions;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(Long id) {
        super("Favorite not found with id: " + id);
    }
}