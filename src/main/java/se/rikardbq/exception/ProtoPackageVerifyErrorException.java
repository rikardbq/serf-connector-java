package se.rikardbq.exception;

public class ProtoPackageVerifyErrorException extends RuntimeException {

    public ProtoPackageVerifyErrorException(String message, String cause) {
        super(message, new Throwable(cause));
    }
}