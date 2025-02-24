package se.rikardbq.models;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FetchResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> data;

    public FetchResponse() {
    }

    public FetchResponse(List<T> data) {
        this.data = data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FetchResponse<T> fetchResponse = (FetchResponse<T>) o;
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
