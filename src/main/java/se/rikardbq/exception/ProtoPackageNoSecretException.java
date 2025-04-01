package se.rikardbq.exception;

public class ProtoPackageNoSecretException extends ProtoPackageErrorException {

    public ProtoPackageNoSecretException() {
        super("No secret", "Proto package missing a secret for signing");
    }
}