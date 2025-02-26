package se.rikardbq.models.migration;

import java.io.Serializable;
import java.util.Objects;

public class Migration implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Object query;

    public Migration() {
    }

    public Migration(String name, Object query) {
        this.name = name;
        this.query = query;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setQuery(Object query) {
        this.query = query;
    }

    public Object getQuery() {
        return query;
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
