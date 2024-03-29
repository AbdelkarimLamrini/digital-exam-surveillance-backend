package be.kdg.backend.exception;

public class NotUniqueException extends RuntimeException {
    public NotUniqueException(String message) {
        super(message);
    }
}
