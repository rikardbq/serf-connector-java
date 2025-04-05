package se.rikardbq.exception;

public class HttpMissingHeaderException extends RuntimeException {

    public HttpMissingHeaderException(String message, String cause) {
        super(message, new Throwable(cause));
    }
}
