package se.rikardbq.exception;

public class MigrationErrorException extends RuntimeException {

    public MigrationErrorException(String message, String cause) {
        super(message, new Throwable(cause));
    }
}
