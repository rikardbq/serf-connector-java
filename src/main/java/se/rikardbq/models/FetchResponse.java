package se.rikardbq.models;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FetchResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Object> data;

    public FetchResponse() {
    }

    public FetchResponse(List<Object> data) {
        this.data = data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FetchResponse fetchResponse = (FetchResponse) o;
        return Objects.equals(data, fetchResponse.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return getClass() + " " + "data=" + data;
    }
}
