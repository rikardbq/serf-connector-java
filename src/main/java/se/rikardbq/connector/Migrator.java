package se.rikardbq.connector;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.rikardbq.models.Enums;
import se.rikardbq.models.MigrationResponse;
import se.rikardbq.models.migration.Migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Migrator {

    private static final String STATE_FILE_CONTENT_HEADER = """
            /* __$gen.serf.state.migrations__ */
            /**
            * THIS FILE IS GENERATED!
            * ------
            * Changing this file may lead to inconsistent state
            * between your application migrations and your database!
            **/""";
    private static final String STATE_FILE_CONTENT_EMPTY = Migrator.createStateFileContent("{\"__applied_migrations__\":[]}");
    private static final String STATE_FILE = "__$gen.serf.state.migrations__.jsonc";
    private static final String STATE_KEY = "__applied_migrations__";

    private String migrationsLocation;
    private Map<String, List<String>> appliedMigrations;

    private ObjectMapper objectMapper;

    public Migrator() {
    }

    public Migrator(String migrationsLocation) {
        this.migrationsLocation = migrationsLocation;
        Path migrationsPath = Path.of(migrationsLocation);
        try {
            if (!Files.exists(migrationsPath)) {
                Files.createDirectory(migrationsPath);
            }

            Path migrationsStatePath = Path.of(migrationsLocation, STATE_FILE);
            if (!Files.exists(migrationsStatePath)) {
                Files.createFile(migrationsStatePath);
                Files.writeString(migrationsStatePath, STATE_FILE_CONTENT_EMPTY);
            }

            this.objectMapper = new ObjectMapper(JsonFactory.builder().configure(JsonReadFeature.ALLOW_JAVA_COMMENTS, true).build());
            this.appliedMigrations = objectMapper.readValue(migrationsStatePath.toFile(), new TypeReference<>() {
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String createStateFileContent(String content) {
        return String.format("%s\n%s", STATE_FILE_CONTENT_HEADER, content);
    }

    public void run(Connector connector) throws Exception {
        List<Migration> migrations = this.prepareMigrations();
        if (!migrations.isEmpty()) {
            for (Migration m : migrations) {
                this.apply(m, connector);
            }
        } else {
            // log something
            System.out.println("NO MIGRATIONS");
        }
    }

    private void apply(Migration migration, Connector connector) throws Exception {
        MigrationResponse response = this.makeMigration(migration, connector);

        if (!response.getState()) {
            throw new Exception("Migration failed");
        }

        this.appliedMigrations.get(STATE_KEY).add(migration.getName());
        Files.writeString(
                Path.of(this.migrationsLocation, STATE_FILE),
                Migrator.createStateFileContent(objectMapper.writeValueAsString(this.appliedMigrations))
        );
    }

    private MigrationResponse makeMigration(Migration migration, Connector connector) throws JsonProcessingException {
        String response = connector.makeRequest(
                this.createMigrationDat(
                        migration.getName(),
                        migration.getQuery()
                ),
                Enums.Subject.MIGRATE, true
        );

        return objectMapper.readValue(response, MigrationResponse.class);
    }

    private String trimFileEnding(Path fileName, String ending) {
        return fileName
                .toString()
                .replace(ending, "");
    }

    private List<Migration> prepareMigrations() {
        try (Stream<Path> files = Files.list(Path.of(this.migrationsLocation))) {
            return files.filter(x ->
                    Files.isRegularFile(x)
                            && x.getFileName().toString().endsWith(".sql")
                            && !this.appliedMigrations.get(STATE_KEY).contains(this.trimFileEnding(x.getFileName(), ".sql"))
            ).map(x -> {
                try {
                    return new Migration(
                            this.trimFileEnding(x.getFileName(), ".sql"),
                            Files.readString(x)
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> createMigrationDat(String name, String query) {
        return Map.ofEntries(
                Map.entry("name", name),
                Map.entry("query", query)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Migrator migrator = (Migrator) o;
        return Objects.equals(migrationsLocation, migrator.migrationsLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(migrationsLocation);
    }

    @Override
    public String toString() {
        return getClass() + " " + "migrationsLocation=" + migrationsLocation;
    }
}
