package se.rikardbq.models;

import java.util.Objects;

public class Migration {

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

    public String getQuery() {
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
