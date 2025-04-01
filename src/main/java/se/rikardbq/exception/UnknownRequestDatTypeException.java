package se.rikardbq.exception;

public class UnknownRequestDatTypeException extends RuntimeException {

    public UnknownRequestDatTypeException() {
        super("Unknown dat type", new Throwable("Unknown request dat type, supported dat types are QueryRequest, MigrationRequest"));
    }
}
