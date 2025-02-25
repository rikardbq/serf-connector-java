package se.rikardbq.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class MutationResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("rows_affected")
    private long rowsAffected;
    @JsonProperty("last_insert_rowid")
    private long lastInsertRowId;

    public MutationResponse() {
    }

    public MutationResponse(long rowsAffected, long lastInsertRowId) {
        this.rowsAffected = rowsAffected;
        this.lastInsertRowId = lastInsertRowId;
    }

    public void setRowsAffected(long rowsAffected) {
        this.rowsAffected = rowsAffected;
    }

    public long getRowsAffected() {
        return rowsAffected;
    }

    public void setLastInsertRowId(long lastInsertRowId) {
        this.lastInsertRowId = lastInsertRowId;
    }

    public long getLastInsertRowId() {
        return lastInsertRowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutationResponse mutationResponse = (MutationResponse) o;
        return rowsAffected == mutationResponse.rowsAffected && lastInsertRowId == mutationResponse.lastInsertRowId;
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
