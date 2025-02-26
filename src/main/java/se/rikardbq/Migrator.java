package se.rikardbq;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Migrator {

    private static final String STATE_FILE_CONTENT_HEADER = """
            /* __$gen.serf.state.migrations__ */
            /**
            * THIS FILE IS GENERATED!
            * ------
            * Changing this file may lead to inconsistent state
            * between your application migrations and your database!
            **/""";
    private static final String STATE_FILE_CONTENT_EMPTY = String.format("%s\n%s", STATE_FILE_CONTENT_HEADER, "{\"__applied_migrations__\":[]}");
    private static final String STATE_FILE = "__$gen.serf.state.migrations__.jsonc";
    private static final String STATE_KEY = "__applied_migrations__";
    private Path migrationsPath;
    private Map<String, List<String>> appliedMigrations;

    private ObjectMapper objectMapper;

    public Migrator() {
    }

    public Migrator(String migrationsPath) {
        this.migrationsPath = Paths.get(migrationsPath);
        try {
            if (!Files.exists(this.migrationsPath)) {
                Files.createDirectory(this.migrationsPath);
            }

            Path migrationsStatePath = Paths.get(migrationsPath, STATE_FILE);
            if (!Files.exists(migrationsStatePath)) {
                Files.createFile(migrationsStatePath);
                Files.writeString(migrationsStatePath, STATE_FILE_CONTENT_EMPTY);
            }

            objectMapper = new ObjectMapper(JsonFactory.builder().configure(JsonReadFeature.ALLOW_JAVA_COMMENTS, true).build());
            this.appliedMigrations = objectMapper.readValue(migrationsStatePath.toFile(), new TypeReference<>() {
            });

            System.out.println(this.appliedMigrations.get(STATE_KEY));

            System.out.println(this.appliedMigrations);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setMigrationsPath(Path migrationsPath) {
        this.migrationsPath = migrationsPath;
    }

    public Path getMigrationsPath() {
        return migrationsPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Migrator migrator = (Migrator) o;
        return Objects.equals(migrationsPath, migrator.migrationsPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(migrationsPath);
    }

    @Override
    public String toString() {
        return getClass() + " " + "migrationsPath=" + migrationsPath;
    }
}
