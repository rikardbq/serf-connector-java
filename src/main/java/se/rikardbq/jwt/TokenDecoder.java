package se.rikardbq.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenDecoder {

    public static DecodedJWT decodeToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256("c22379960d253fa34b58c16f898807af287f9c838768279a10dca5d43d6e8682");
        JWTVerifier verifier = JWT.require(algorithm)
                // specify any specific claim validations
                .withIssuer("SERVER")
                .withSubject("DATA")
                // reusable verifier instance
                .build();

        return verifier.verify(token);
    }
}
