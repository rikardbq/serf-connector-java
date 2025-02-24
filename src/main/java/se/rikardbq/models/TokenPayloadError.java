package se.rikardbq.models;

import java.io.Serializable;
import java.util.Objects;

public class TokenPayloadError implements Serializable {
    private static long serialVersionUID = 1L;

    private String message;

    public TokenPayloadError() {
    }

    public TokenPayloadError(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPayloadError tokenPayloadError = (TokenPayloadError) o;
        return Objects.equals(message, tokenPayloadError.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return getClass() + " " + "message=" + message;
    }
}
