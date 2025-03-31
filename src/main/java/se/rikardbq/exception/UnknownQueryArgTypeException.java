package se.rikardbq.exception;

public class UnknownQueryArgTypeException extends RuntimeException {

    public UnknownQueryArgTypeException() {
        super("Invalid type", new Throwable("Invalid type for QueryArg, supported types are [ Integer, Long, Float, Double, String, byte[](Array of bytes) ]"));
    }
}