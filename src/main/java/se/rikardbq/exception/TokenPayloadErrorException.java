package se.rikardbq.exception;

import se.rikardbq.models.TokenPayloadError;

public class TokenPayloadErrorException extends RuntimeException {

    public TokenPayloadErrorException(TokenPayloadError tokenPayloadError) {
        super(tokenPayloadError.getMessage(), new Throwable(tokenPayloadError.getSource()));
    }
}