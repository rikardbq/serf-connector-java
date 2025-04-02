package se.rikardbq.models;

import java.util.Objects;

public class MutationResponse {

    private final long rowsAffected;
    private final long lastInsertRowId;

    public MutationResponse(long rowsAffected, long lastInsertRowId) {
        this.rowsAffected = rowsAffected;
        this.lastInsertRowId = lastInsertRowId;
    }

    public long getRowsAffected() {
        return rowsAffected;
    }

    public long getLastInsertRowId() {
        return lastInsertRowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutationResponse mutationResponse = (MutationResponse) o;
        return rowsAffected == mutationResponse.rowsAffected
                && lastInsertRowId == mutationResponse.lastInsertRowId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowsAffected, lastInsertRowId);
    }

    @Override
    public String toString() {
        return getClass() + " " + "rowsAffected=" + rowsAffected + ", lastInsertRowId=" + lastInsertRowId;
    }
}
