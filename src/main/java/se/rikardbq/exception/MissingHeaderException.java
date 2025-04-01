package se.rikardbq.exception;

public class MissingHeaderException extends RuntimeException {

    public MissingHeaderException(String message, String cause) {
        super(message, new Throwable(cause));
    }
}
