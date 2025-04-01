package se.rikardbq.proto;

import se.rikardbq.exception.ProtoPackageInvalidSubjectException;
import se.rikardbq.exception.ProtoPackageNoSecretException;
import se.rikardbq.exception.ProtoPackageUnexpectedDatTypeException;
import se.rikardbq.exception.ProtoPackageErrorException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class ProtoPackage {

    private final byte[] data;
    private final String signature;

    private ProtoPackage(byte[] data, String signature) {
        this.data = data;
        this.signature = signature;
    }

    public byte[] getData() {
        return this.data;
    }

    public String getSignature() {
        return this.signature;
    }

    public static class Builder {
        private Object dat;
        private ClaimsUtil.Sub sub = ClaimsUtil.Sub.UNRECOGNIZED;

        public ProtoPackage.Builder withData(Object dat) {
            this.dat = dat;

            return this;
        }

        public ProtoPackage.Builder withSubject(ClaimsUtil.Sub sub) {
            this.sub = sub;

            return this;
        }

        public ProtoPackage sign(String secret) throws ProtoPackageErrorException {
            if (this.sub == ClaimsUtil.Sub.UNRECOGNIZED) {
                throw new ProtoPackageInvalidSubjectException();
            }

            if (Objects.isNull(secret)) {
                throw new ProtoPackageNoSecretException();
            }

            ProtoRequest.Claims.Builder claimsBuilder = ProtoRequest.Claims.newBuilder();
            Instant now = Instant.now();
            claimsBuilder
                    .setIss(ClaimsUtil.Iss.CLIENT)
                    .setSub(sub)
                    .setIat(now.getEpochSecond())
                    .setExp(now.plusSeconds(30).getEpochSecond());

            switch (this.dat) {
//            case FetchResponseOuterClass.FetchResponse v -> claimsBuilder.setFetchResponse(v);
//            case MigrationResponseOuterClass.MigrationResponse v -> claimsBuilder.setMigrationResponse(v);
//            case MutationResponseOuterClass.MutationResponse v -> claimsBuilder.setMutationResponse(v);
                case ClaimsUtil.MigrationRequest v -> claimsBuilder.setMigrationRequest(v);
                case ClaimsUtil.QueryRequest v -> claimsBuilder.setQueryRequest(v);
                default -> throw new ProtoPackageUnexpectedDatTypeException();
            }
            ProtoRequest.Request.Builder protoRequestBuilder = ProtoRequest.Request.newBuilder();
            protoRequestBuilder.setClaims(claimsBuilder.build());
            byte[] data = protoRequestBuilder.build().toByteArray();

            return new ProtoPackage(data, ProtoPackageUtil.generateSignature(data, secret.getBytes(StandardCharsets.UTF_8)));
        }
    }
}
