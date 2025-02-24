package se.rikardbq.models;

import java.io.Serializable;
import java.util.Objects;

public class TokenPayloadError implements Serializable {
    private static long serialVersionUID = 1L;

    private String message;
    private String source;

    public TokenPayloadError() {
    }

    public TokenPayloadError(String message, String source) {
        this.message = message;
        this.source = source;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPayloadError tokenPayloadError = (TokenPayloadError) o;
        return Objects.equals(message, tokenPayloadError.message) && Objects.equals(source, tokenPayloadError.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, source);
    }

    @Override
    public String toString() {
        return getClass() + " " + "message=" + message + ", source=" + source;
    }
}
