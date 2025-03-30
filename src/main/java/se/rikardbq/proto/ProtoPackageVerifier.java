package se.rikardbq.proto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class ProtoPackageVerifier {

    private final String signature;
    private final String secret;
    private final ClaimsUtil.Sub sub;
    private final ClaimsUtil.Iss iss;

    private ProtoPackageVerifier(String signature, String secret, ClaimsUtil.Sub sub, ClaimsUtil.Iss iss) {
        this.signature = signature;
        this.secret = secret;
        this.sub = sub;
        this.iss = iss;
    }

    private boolean verifySignature(byte[] data, String signature, byte[] secret) {
        return Objects.equals(signature, ProtoPackageUtil.generateSignature(data, secret));
    }

    public ProtoRequest.Claims verify(byte[] data) throws Exception {
        if (!verifySignature(data, this.signature, this.secret.getBytes(StandardCharsets.UTF_8))) {
            throw new Exception("verify error");
        }

        ProtoRequest.Claims claims = ProtoRequest.Claims.newBuilder()
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
        private ClaimsUtil.Sub sub;
        private ClaimsUtil.Iss iss;

        public ProtoPackageVerifier.Builder withSecret(String secret) {
            this.secret = secret;

            return this;
        }

        public ProtoPackageVerifier.Builder withSignature(String signature) {
            this.signature = signature;

            return this;
        }

        public ProtoPackageVerifier.Builder withSubject(ClaimsUtil.Sub sub) {
            this.sub = sub;

            return this;
        }

        public ProtoPackageVerifier.Builder withIssuer(ClaimsUtil.Iss iss) {
            this.iss = iss;

            return this;
        }

        public ProtoPackageVerifier build() {
            return new ProtoPackageVerifier(this.signature, this.secret, this.sub, this.iss);
        }
    }
}
