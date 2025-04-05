package se.rikardbq.exception;

public class ProtoPackageInvalidSubjectException extends ProtoPackageErrorException {

    public ProtoPackageInvalidSubjectException() {
        super("Invalid subject", "Claims sub field not set or not matching required value");
    }
}