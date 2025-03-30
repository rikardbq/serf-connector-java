package se.rikardbq.exception;

public class ProtoRequestInvalidSignatureErrorException extends ProtoPackageVerifyErrorException {

    public ProtoRequestInvalidSignatureErrorException() {
        super("Invalid signature", "Request signature does not match");
    }
}