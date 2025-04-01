package se.rikardbq.exception;

public class ProtoPackageClaimsExpiredException extends ProtoPackageErrorException {

    public ProtoPackageClaimsExpiredException() {
        super("Claims expired", "Claims exp field no longer valid");
    }
}