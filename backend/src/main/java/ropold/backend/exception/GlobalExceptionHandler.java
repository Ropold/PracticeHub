package ropold.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RoomError handleRoomNotFoundException(RoomNotFoundException e) {
        return new RoomError(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RoomError handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return new RoomError(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    // Handler für die benutzerdefinierte Ausnahme ImageDeletionException
    @ExceptionHandler(ImageDeletionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500 für Serverfehler
    public RoomError handleImageDeletionException(ImageDeletionException e) {
        return new RoomError(e.getMessage()); // Fehlermeldung aus der Exception
    }

}
