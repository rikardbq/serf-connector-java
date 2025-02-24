package se.rikardbq.models;

import java.io.Serializable;
import java.util.Objects;

public class MigrationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String query;

    public MigrationRequest() {
    }

    public MigrationRequest(String name, String query) {
        this.name = name;
        this.query = query;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MigrationRequest migrationRequest = (MigrationRequest) o;
        return Objects.equals(name, migrationRequest.name) && Objects.equals(query, migrationRequest.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, query);
    }

    @Override
    public String toString() {
        return getClass() + " " + "name=" + name + ", query=" + query;
    }
}
