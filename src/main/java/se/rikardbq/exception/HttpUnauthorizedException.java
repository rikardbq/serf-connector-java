package se.rikardbq.exception;

public class HttpUnauthorizedException extends RuntimeException {

    public HttpUnauthorizedException(String message) {
        super(message, new Throwable("Server responded with 401 Unauthorized"));
    }
}
