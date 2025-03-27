package proto;

import serf_proto.ClaimsOuterClass;
import serf_proto.MigrationRequestOuterClass;
import serf_proto.QueryRequestOuterClass;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class ProtoPackage {

    private final byte[] data;
    private final String signature;

    private ProtoPackage(byte[] data, byte[] secret) {
        this.data = data;
        this.signature = ProtoUtil.generateSignature(data, secret);
    }

    public byte[] getData() {
        return this.data;
    }

    public String getSignature() {
        return this.signature;
    }

    public static class Builder {
        private Object dat;
        private ClaimsOuterClass.Sub sub = ClaimsOuterClass.Sub.UNRECOGNIZED;

        public ProtoPackage.Builder withData(Object dat) {
            this.dat = dat;

            return this;
        }

        public ProtoPackage.Builder withSubject(ClaimsOuterClass.Sub sub) {
            this.sub = sub;

            return this;
        }

        public ProtoPackage sign(String secret) throws Exception {
            if (this.sub == ClaimsOuterClass.Sub.UNRECOGNIZED) {
                throw new Exception("no subject error");
            }

            if (Objects.isNull(secret)) {
                throw new Exception("no secret error");
            }

            ClaimsOuterClass.Claims.Builder claimsBuilder = ClaimsOuterClass.Claims.newBuilder();
            Instant now = Instant.now();
            claimsBuilder
                    .setIss(ClaimsOuterClass.Iss.CLIENT)
                    .setSub(sub)
                    .setIat(now.getEpochSecond())
                    .setExp(now.plusSeconds(30).getEpochSecond());

            switch (this.dat) {
//            some are not needed from this side of the transaction. Only the request parts are important.. possibly... I NEED TO THINK OK!?
//            case FetchResponseOuterClass.FetchResponse v -> claimsBuilder.setFetchResponse(v);
//            case MigrationResponseOuterClass.MigrationResponse v -> claimsBuilder.setMigrationResponse(v);
//            case MutationResponseOuterClass.MutationResponse v -> claimsBuilder.setMutationResponse(v);
                case MigrationRequestOuterClass.MigrationRequest v -> claimsBuilder.setMigrationRequest(v);
                case QueryRequestOuterClass.QueryRequest v -> claimsBuilder.setQueryRequest(v);
                default -> throw new Exception("dat type error"); // replace this with something more intuitive
            }

            return new ProtoPackage(claimsBuilder.build().toByteArray(), secret.getBytes(StandardCharsets.UTF_8));
        }
    }
}
