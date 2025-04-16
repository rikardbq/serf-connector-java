package se.rikardbq.exception;

public class ProtoPackageVerifierMissingSignatureException extends ProtoPackageErrorException {

    public ProtoPackageVerifierMissingSignatureException() {
        super("Verifier missing signature", "No signature was supplied to the verifier");
    }
}