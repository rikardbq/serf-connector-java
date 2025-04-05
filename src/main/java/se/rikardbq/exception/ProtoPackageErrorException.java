package se.rikardbq.exception;

public class ProtoPackageErrorException extends RuntimeException {

    public ProtoPackageErrorException(String message, String cause) {
        super(message, new Throwable(cause));
    }
}