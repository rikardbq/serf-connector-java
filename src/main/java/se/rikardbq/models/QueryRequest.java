package se.rikardbq.models;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class QueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String query;
    private List<Object> parts;

    public QueryRequest() {
    }

    public QueryRequest(String query, List<Object> parts) {
        this.query = query;
        this.parts = parts;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setParts(List<Object> parts) {
        this.parts = parts;
    }

    public List<Object> getParts() {
        return parts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryRequest queryRequest = (QueryRequest) o;
        return Objects.equals(query, queryRequest.query) && Objects.equals(parts, queryRequest.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, parts);
    }

    @Override
    public String toString() {
        return getClass() + " " + "query=" + query + ", parts=" + parts;
    }
}
