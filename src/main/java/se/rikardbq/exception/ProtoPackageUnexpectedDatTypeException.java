package se.rikardbq.exception;

public class ProtoPackageUnexpectedDatTypeException extends ProtoPackageVerifyErrorException {

    public ProtoPackageUnexpectedDatTypeException() {
        super("Unexpected dat type", "Claims dat field received unexpected type");
    }
}