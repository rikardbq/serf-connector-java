package se.rikardbq.proto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class ProtoPackage {

    private final byte[] data;
    private final String signature;

    private ProtoPackage(byte[] data, byte[] secret) {
        this.data = data;
        this.signature = ProtoPackageUtil.generateSignature(data, secret);
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

        public ProtoPackage sign(String secret) throws Exception {
            if (this.sub == ClaimsUtil.Sub.UNRECOGNIZED) {
                throw new Exception("no subject error");
            }

            if (Objects.isNull(secret)) {
                throw new Exception("no secret error");
            }

            ProtoRequest.Claims.Builder claimsBuilder = ProtoRequest.Claims.newBuilder();
            Instant now = Instant.now();
            claimsBuilder
                    .setIss(ClaimsUtil.Iss.CLIENT)
                    .setSub(sub)
                    .setIat(now.getEpochSecond())
                    .setExp(now.plusSeconds(30).getEpochSecond());

            switch (this.dat) {
//            some are not needed from this side of the transaction. Only the request parts are important.. possibly... I NEED TO THINK OK!?
//            case FetchResponseOuterClass.FetchResponse v -> claimsBuilder.setFetchResponse(v);
//            case MigrationResponseOuterClass.MigrationResponse v -> claimsBuilder.setMigrationResponse(v);
//            case MutationResponseOuterClass.MutationResponse v -> claimsBuilder.setMutationResponse(v);
                case ClaimsUtil.MigrationRequest v -> claimsBuilder.setMigrationRequest(v);
                case ClaimsUtil.QueryRequest v -> claimsBuilder.setQueryRequest(v);
                default -> throw new Exception("dat type error"); // replace this with something more intuitive
            }
            ProtoRequest.Request.Builder protoRequestBuilder = ProtoRequest.Request.newBuilder();
            protoRequestBuilder.setClaims(claimsBuilder.build());

            return new ProtoPackage(protoRequestBuilder.build().toByteArray(), secret.getBytes(StandardCharsets.UTF_8));
        }
    }
}
