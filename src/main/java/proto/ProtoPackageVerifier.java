package proto;

import serf_proto.ClaimsOuterClass;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class ProtoPackageVerifier {

    private final String signature;
    private final String secret;
    private final ClaimsOuterClass.Sub sub;
    private final ClaimsOuterClass.Iss iss;

    private ProtoPackageVerifier(String signature, String secret, ClaimsOuterClass.Sub sub, ClaimsOuterClass.Iss iss) {
        this.signature = signature;
        this.secret = secret;
        this.sub = sub;
        this.iss = iss;
    }

    private boolean verifySignature(byte[] data, String signature, byte[] secret) {
        return Objects.equals(signature, ProtoUtil.generateSignature(data, secret));
    }

    public ClaimsOuterClass.Claims verify(byte[] data) throws Exception {
        if (!verifySignature(data, this.signature, this.secret.getBytes(StandardCharsets.UTF_8))) {
            throw new Exception("verify error");
        }

        ClaimsOuterClass.Claims claims = ClaimsOuterClass.Claims.newBuilder()
                .mergeFrom(data)
                .build();

        if (Objects.nonNull(this.sub) && claims.getSub() != this.sub) {
            throw new Exception("verify subject error");
        }

        if (Objects.nonNull(this.iss) && claims.getIss() != this.iss) {
            throw new Exception("verify issuer error");
        }

        Instant now = Instant.now();
        if (now.getEpochSecond() > claims.getExp()) {
            throw new Exception("verify expiration error");
        }

        return claims;
    }

    public static class Builder {

        private String signature;
        private String secret;
        private ClaimsOuterClass.Sub sub;
        private ClaimsOuterClass.Iss iss;

        public ProtoPackageVerifier.Builder withSecret(String secret) {
            this.secret = secret;

            return this;
        }

        public ProtoPackageVerifier.Builder withSignature(String signature) {
            this.signature = signature;

            return this;
        }

        public ProtoPackageVerifier.Builder withSubject(ClaimsOuterClass.Sub sub) {
            this.sub = sub;

            return this;
        }

        public ProtoPackageVerifier.Builder withIssuer(ClaimsOuterClass.Iss iss) {
            this.iss = iss;

            return this;
        }

        public ProtoPackageVerifier build() {
            return new ProtoPackageVerifier(this.signature, this.secret, this.sub, this.iss);
        }
    }
}
