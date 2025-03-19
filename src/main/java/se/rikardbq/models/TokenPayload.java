package se.rikardbq.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String payload;
    private TokenPayloadError error;

    public TokenPayload() {
    }

    public TokenPayload(String payload, TokenPayloadError error) {
        this.payload = payload;
        this.error = error;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public TokenPayloadError getError() {
        return error;
    }

    public void setError(TokenPayloadError error) {
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPayload tokenPayload = (TokenPayload) o;
        return Objects.equals(payload, tokenPayload.payload) && Objects.equals(error, tokenPayload.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload, error);
    }

    @Override
    public String toString() {
        return getClass() + " " + "payload=" + payload + ", error=" + error;
    }
}
