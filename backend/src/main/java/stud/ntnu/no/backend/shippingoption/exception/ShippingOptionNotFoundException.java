package stud.ntnu.no.backend.shippingoption.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a shipping option is not found.
 *
 * <p>This exception is mapped to a 404 Not Found HTTP status code.
 */
public class ShippingOptionNotFoundException extends BaseException {

  /**
   * Constructs a new ShippingOptionNotFoundException with the specified shipping option ID.
   *
   * @param id the ID of the shipping option that was not found
   */
  public ShippingOptionNotFoundException(Long id) {
    super("Could not find shipping option with id: " + id);
  }
}
