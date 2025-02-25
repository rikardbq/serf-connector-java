package se.rikardbq.models;

import java.io.Serializable;
import java.util.Objects;

public class ResponseClaims<T> implements Serializable {
    private static long serialVersionUID = 1L;

    private Enums.Issuer iss;
    private Enums.Subject sub;
    private T dat;
    private long iat;
    private long exp;

    public ResponseClaims() {
    }

    public ResponseClaims(Enums.Issuer iss, Enums.Subject sub, T dat, long iat, long exp) {
        this.iss = iss;
        this.sub = sub;
        this.dat = dat;
        this.iat = iat;
        this.exp = exp;
    }

    public void setIss(Enums.Issuer iss) {
        this.iss = iss;
    }

    public Enums.Issuer getIss() {
        return iss;
    }

    public void setSub(Enums.Subject sub) {
        this.sub = sub;
    }

    public Enums.Subject getSub() {
        return sub;
    }

    public void setDat(T dat) {
        this.dat = dat;
    }

    public T getDat() {
        return dat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getIat() {
        return iat;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getExp() {
        return exp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseClaims<T> responseClaims = (ResponseClaims<T>) o;
        return Objects.equals(iss, responseClaims.iss) && Objects.equals(sub, responseClaims.sub) && Objects.equals(dat, responseClaims.dat) && iat == responseClaims.iat && exp == responseClaims.exp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(iss, sub, dat, iat, exp);
    }

    @Override
    public String toString() {
        return getClass() + " " + "iss=" + iss + ", sub=" + sub + ", dat=" + dat + ", iat=" + iat + ", exp=" + exp;
    }
}


/**
 * {
 * "iss": "SERVER",
 * "sub": "FETCH",
 * "dat": {
 * "query": "SELECT * FROM testing_table;",
 * "parts": []
 * },
 * "iat": 1740396579,
 * "exp": 1740396609
 * }
 */