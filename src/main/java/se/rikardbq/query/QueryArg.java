// may remove this as its not really needed with Object being a thing and all
package se.rikardbq.query;

import java.io.Serializable;
import java.util.Objects;

public class QueryArg<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T value;

    public QueryArg() {
    }

    public QueryArg(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryArg<T> queryArg = (QueryArg<T>) o;
        return Objects.equals(value, queryArg.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return getClass() + " " + "value=" + value;
    }
}
