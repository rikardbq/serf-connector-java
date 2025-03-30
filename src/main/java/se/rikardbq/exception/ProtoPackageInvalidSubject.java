package se.rikardbq.exception;

public class ProtoPackageInvalidSubject extends ProtoPackageVerifyErrorException {

    public ProtoPackageInvalidSubject() {
        super("Invalid subject", "Claims sub field not set or not matching required value");
    }
}