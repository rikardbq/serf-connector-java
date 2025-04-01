package se.rikardbq.connector;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import se.rikardbq.exception.MigrationFailedException;
import se.rikardbq.exception.MissingHeaderException;
import se.rikardbq.exception.ProtoPackageErrorException;
import se.rikardbq.models.migration.Migration;
import se.rikardbq.proto.ClaimsUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Migrator {

    private static final String STATE_FILE = "__$gen.serf.state.migrations__.jsonc";
    private static final String STATE_KEY = "__applied_migrations__";

    private final String migrationsLocation;
    private Map<String, List<String>> appliedMigrations;

    private ObjectMapper objectMapper;

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
                this.writeStateFile("{\"__applied_migrations__\":[]}");
            }

            this.objectMapper = new ObjectMapper(
                    JsonFactory.builder().configure(
                            JsonReadFeature.ALLOW_JAVA_COMMENTS,
                            true
                    ).build()
            );
            this.appliedMigrations = this.objectMapper.readValue(migrationsStatePath.toFile(), new TypeReference<>() {
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private void apply(Migration migration, Connector connector) throws MigrationFailedException, IOException, MissingHeaderException, ProtoPackageErrorException {
        ClaimsUtil.MigrationResponse response = this.makeMigration(migration, connector);

        if (!response.getState()) {
            throw new MigrationFailedException();
        }

        this.appliedMigrations.get(STATE_KEY).add(migration.getName());
        this.writeStateFile(this.objectMapper.writeValueAsString(this.appliedMigrations));
    }

    private ClaimsUtil.MigrationResponse makeMigration(Migration migration, Connector connector) throws InvalidProtocolBufferException, MissingHeaderException, ProtoPackageErrorException {

        ClaimsUtil.MigrationRequest.Builder migrationRequestBuilder = ClaimsUtil.MigrationRequest.newBuilder()
                .setName(migration.getName())
                .setQuery(migration.getQuery());

        return (ClaimsUtil.MigrationResponse) connector.makeRequest(
                migrationRequestBuilder.build(),
                ClaimsUtil.Sub.MIGRATE,
                true
        );
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

    private void writeStateFile(String content) throws IOException {
        String STATE_FILE_CONTENT_HEADER = """
                /* __$gen.serf.state.migrations__ */
                /**
                * THIS FILE IS GENERATED!
                * ------
                * Changing this file may lead to inconsistent state
                * between your application migrations and your database!
                **/""";

        Files.writeString(
                Path.of(this.migrationsLocation, STATE_FILE),
                String.format("%s\n%s", STATE_FILE_CONTENT_HEADER, content)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Migrator migrator = (Migrator) o;
        return Objects.equals(migrationsLocation, migrator.migrationsLocation)
                && Objects.equals(appliedMigrations, migrator.appliedMigrations)
                && Objects.equals(objectMapper, migrator.objectMapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(migrationsLocation, appliedMigrations, objectMapper);
    }

//    enable this for debug purposes
//    @Override
//    public String toString() {
//        return getClass() + " " + "migrationsLocation=" + migrationsLocation + ", appliedMigrations=" + appliedMigrations
//                + ", objectMapper=" + objectMapper;
//    }
}
