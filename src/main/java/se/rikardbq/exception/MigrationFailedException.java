package se.rikardbq.exception;

public class MigrationFailedException extends MigrationErrorException {

    public MigrationFailedException() {
        super("Migration failed", "Failed to apply migration due to server error");
    }
}
