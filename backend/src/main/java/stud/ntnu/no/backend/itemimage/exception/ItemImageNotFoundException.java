package stud.ntnu.no.backend.itemimage.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/** Exception thrown when an item image is not found. Responds with HTTP status NOT_FOUND. */
public class ItemImageNotFoundException extends BaseException {

  /**
   * Constructs a new ItemImageNotFoundException with the specified item image ID.
   *
   * @param id The ID of the item image that was not found
   */
  public ItemImageNotFoundException(Long id) {
    super("Could not find item image with id: " + id);
  }
}
