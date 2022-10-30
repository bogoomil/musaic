package hu.boga.musaic.core.exceptions;

public class MusaicException extends RuntimeException{
    public MusaicException(String message, Throwable cause) {
        super(message, cause);
    }

    public MusaicException(String message) {
        super(message);
    }
}
