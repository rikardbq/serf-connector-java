package se.rikardbq.exception;

public class ProtoPackageVerifierMissingSecretException extends ProtoPackageErrorException {

    public ProtoPackageVerifierMissingSecretException() {
        super("Verifier missing secret", "No secret was supplied to the verifier");
    }
}