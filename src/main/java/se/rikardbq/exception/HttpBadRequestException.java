package se.rikardbq.exception;

public class HttpBadRequestException extends RuntimeException {

    public HttpBadRequestException(String message) {
        super(message, new Throwable("Server responded with 400 Bad Request"));
    }
}
