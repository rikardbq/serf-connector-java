package se.rikardbq.models;

public class Enums {
    public enum Issuer {
        CLIENT,
        SERVER
    }

    public enum Subject {
        DATA,
        FETCH,
        MIGRATE,
        MUTATE
    }
}
