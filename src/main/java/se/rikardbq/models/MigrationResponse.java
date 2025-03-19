package se.rikardbq.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class MigrationResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean state;

    public MigrationResponse() {
    }

    public MigrationResponse(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MigrationResponse migrationResponse = (MigrationResponse) o;
        return state == migrationResponse.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    @Override
    public String toString() {
        return getClass() + " " + "state=" + state;
    }
}
