package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable exception) {
        super(message, exception);
    }
}
