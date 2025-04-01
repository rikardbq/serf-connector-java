package se.rikardbq.exception;

public class ProtoPackageInvalidSubjectException extends ProtoPackageVerifyErrorException {

    public ProtoPackageInvalidSubjectException() {
        super("Invalid subject", "Claims sub field not set or not matching required value");
    }
}