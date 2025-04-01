package se.rikardbq.exception;

public class ProtoPackageNoSecretException extends ProtoPackageVerifyErrorException {

    public ProtoPackageNoSecretException() {
        super("No secret", "Proto package missing a secret for signing");
    }
}