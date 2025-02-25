package se.rikardbq.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import se.rikardbq.models.Enums;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TokenManager {

    public TokenManager() {
    }

    public DecodedJWT decodeToken(String token, String secret) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(Enums.Issuer.SERVER.name())
                .withSubject(Enums.Subject.DATA.name())
                .build();

        return verifier.verify(token);
    }

    public String encodeToken(Map<String, Object> dat, Enums.Subject subject, String secret) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Instant now = Instant.now();

        return JWT.create()
                .withIssuer(Enums.Issuer.CLIENT.name())
                .withSubject(subject.name())
                .withClaim("dat", dat)
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(30))
                .sign(algorithm);
    }

    public static class DatBuilder {
        private static final Map<String, Object> datClaim = new HashMap<>();

        public DatBuilder withField(String k, Object v) {
            datClaim.put(k, v);
            return this;
        }

        public Map<String, Object> build() {
            return datClaim;
        }
    }
}

