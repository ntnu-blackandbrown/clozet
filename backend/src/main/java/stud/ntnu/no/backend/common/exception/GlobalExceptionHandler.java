package stud.ntnu.no.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import stud.ntnu.no.backend.history.exception.HistoryNotFoundException;
import stud.ntnu.no.backend.history.exception.HistoryValidationException;
import stud.ntnu.no.backend.item.exception.ItemNotFoundException;
import stud.ntnu.no.backend.item.exception.ItemValidationException;
import stud.ntnu.no.backend.category.exception.CategoryNotFoundException;
import stud.ntnu.no.backend.category.exception.CategoryValidationException;
import stud.ntnu.no.backend.favorite.exception.FavoriteNotFoundException;
import stud.ntnu.no.backend.favorite.exception.FavoriteValidationException;
import stud.ntnu.no.backend.itemimage.exception.ItemImageNotFoundException;
import stud.ntnu.no.backend.itemimage.exception.ItemImageValidationException;
import stud.ntnu.no.backend.location.exception.LocationNotFoundException;
import stud.ntnu.no.backend.location.exception.LocationValidationException;
import stud.ntnu.no.backend.message.exception.MessageNotFoundException;
import stud.ntnu.no.backend.transaction.exception.TransactionNotFoundException;
import stud.ntnu.no.backend.transaction.exception.TransactionValidationException;
import stud.ntnu.no.backend.user.exception.*;
import stud.ntnu.no.backend.review.exception.ReviewNotFoundException;
import stud.ntnu.no.backend.review.exception.ReviewValidationException;
import stud.ntnu.no.backend.shippingoption.exception.ShippingOptionNotFoundException;
import stud.ntnu.no.backend.shippingoption.exception.ShippingOptionValidationException;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Review exception handlers
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Object> handleReviewNotFoundException(ReviewNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReviewValidationException.class)
    public ResponseEntity<Object> handleReviewValidationException(ReviewValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Category exception handlers
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Object> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryValidationException.class)
    public ResponseEntity<Object> handleCategoryValidationException(CategoryValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // History exception handlers
    @ExceptionHandler(HistoryNotFoundException.class)
    public ResponseEntity<Object> handleHistoryNotFoundException(HistoryNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HistoryValidationException.class)
    public ResponseEntity<Object> handleHistoryValidationException(HistoryValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // User exception handlers
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Object> handleUserValidationException(UserValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<Object> handleEmailAlreadyInUseException(EmailAlreadyInUseException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Generic exception handlers
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        return buildErrorResponse("Unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }

    // Message exception handler
    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<Object> handleMessageNotFoundException(MessageNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Transaction exception handler
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Object> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Favorite exception handler
    @ExceptionHandler(FavoriteNotFoundException.class)
    public ResponseEntity<Object> handleFavoriteNotFoundException(FavoriteNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Location exception handlers
    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<Object> handleLocationNotFoundException(LocationNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LocationValidationException.class)
    public ResponseEntity<Object> handleLocationValidationException(LocationValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Item image exception handlers
    @ExceptionHandler(ItemImageNotFoundException.class)
    public ResponseEntity<Object> handleItemImageNotFoundException(ItemImageNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemImageValidationException.class)
    public ResponseEntity<Object> handleItemImageValidationException(ItemImageValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Item exception handlers
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFoundException(ItemNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemValidationException.class)
    public ResponseEntity<Object> handleItemValidationException(ItemValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Authentication exception handler
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }



    // ShippingOption exception handlers
    @ExceptionHandler(ShippingOptionNotFoundException.class)
    public ResponseEntity<Object> handleShippingOptionNotFoundException(ShippingOptionNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ShippingOptionValidationException.class)
    public ResponseEntity<Object> handleShippingOptionValidationException(ShippingOptionValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionValidationException.class)
    public ResponseEntity<Object> handleTransactionValidationException(TransactionValidationException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FavoriteValidationException.class)
    public ResponseEntity<Object> handleFavoriteValidationException(FavoriteValidationException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, status);
    }

}