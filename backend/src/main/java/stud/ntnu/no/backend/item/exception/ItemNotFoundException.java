package stud.ntnu.no.backend.item.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

public class ItemNotFoundException extends BaseException {

  public ItemNotFoundException(Long id) {
    super("Item not found with id: " + id);
  }

  public ItemNotFoundException(String message) {
    super(message);
  }
}
