package se.rikardbq.exception;

public class ProtoRequestInvalidSignatureErrorException extends ProtoPackageErrorException {

    public ProtoRequestInvalidSignatureErrorException() {
        super("Invalid signature", "Request signature does not match");
    }
}