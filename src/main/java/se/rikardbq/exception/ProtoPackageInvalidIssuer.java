package se.rikardbq.exception;

public class ProtoPackageInvalidIssuer extends ProtoPackageVerifyErrorException {

    public ProtoPackageInvalidIssuer() {
        super("Invalid issuer", "Claims iss field not set or not matching required value");
    }
}