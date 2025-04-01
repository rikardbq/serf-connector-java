package se.rikardbq.exception;

public class ProtoPackageInvalidIssuerException extends ProtoPackageVerifyErrorException {

    public ProtoPackageInvalidIssuerException() {
        super("Invalid issuer", "Claims iss field not set or not matching required value");
    }
}