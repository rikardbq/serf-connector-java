package se.rikardbq.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Migration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String query;

    public Migration() {
    }

    public Migration(String name, String query) {
        this.name = name;
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Migration migration = (Migration) o;
        return Objects.equals(name, migration.name) && Objects.equals(query, migration.query);
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
