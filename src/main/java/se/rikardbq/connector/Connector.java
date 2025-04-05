package se.rikardbq.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.digest.DigestUtils;
import se.rikardbq.exception.*;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.proto.ClaimsUtil;
import se.rikardbq.proto.ProtoManager;
import se.rikardbq.proto.ProtoPackage;
import se.rikardbq.proto.ProtoRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Connector {

    private final String fullAddress;
    private final String usernameHash;
    private final String usernamePasswordHash;

    private final ProtoManager protoManager;
    private final ObjectMapper objectMapper;

    public Connector(String address, String database, String username, String password) {
        this.protoManager = new ProtoManager();
        this.objectMapper = new ObjectMapper();
        String hashedDatabase = DigestUtils.sha256Hex(database);
        String hashedUsername = DigestUtils.sha256Hex(username);
        String hashedUsernamePassword = DigestUtils.sha256Hex(username + password);

        this.fullAddress = String.format("%s/%s", address, hashedDatabase);
        this.usernameHash = hashedUsername;
        this.usernamePasswordHash = hashedUsernamePassword;
    }

    public <T> List<T> query(Class<T> valueType, String query, Object... parts) throws IOException, HttpBadRequestException, HttpUnauthorizedException, HttpMissingHeaderException, ProtoPackageErrorException {
        return makeQuery(query, parts, valueType);
    }

    private <T> List<T> makeQuery(String query, Object[] parts, Class<T> valueType) throws IOException, HttpBadRequestException, HttpUnauthorizedException, HttpMissingHeaderException, ProtoPackageErrorException {
        ClaimsUtil.QueryRequest.Builder queryRequestBuilder = ClaimsUtil.QueryRequest.newBuilder()
                .setQuery(query)
                .addAllParts(this.mapPartsToQueryArgs(parts));

        ClaimsUtil.FetchResponse response = (ClaimsUtil.FetchResponse) this.makeRequest(
                queryRequestBuilder.build(),
                ClaimsUtil.Sub.FETCH,
                false
        );

        return this.objectMapper.readValue(
                response.getData().toByteArray(),
                this.objectMapper.getTypeFactory().constructParametricType(List.class, valueType)
        );
    }

    public MutationResponse mutate(String query, Object... parts) throws InvalidProtocolBufferException, HttpBadRequestException, HttpUnauthorizedException, HttpMissingHeaderException, ProtoPackageErrorException {
        ClaimsUtil.MutationResponse mutationResponse = makeMutation(query, parts);

        return new MutationResponse(mutationResponse.getRowsAffected(), mutationResponse.getLastInsertRowId());
    }

    private ClaimsUtil.MutationResponse makeMutation(String query, Object[] parts) throws InvalidProtocolBufferException, HttpBadRequestException, HttpUnauthorizedException, HttpMissingHeaderException, ProtoPackageErrorException {
        ClaimsUtil.QueryRequest.Builder queryRequestBuilder = ClaimsUtil.QueryRequest.newBuilder()
                .setQuery(query)
                .addAllParts(this.mapPartsToQueryArgs(parts));

        return (ClaimsUtil.MutationResponse) this.makeRequest(
                queryRequestBuilder.build(),
                ClaimsUtil.Sub.MUTATE,
                false
        );
    }

    private List<ClaimsUtil.QueryArg> mapPartsToQueryArgs(Object[] parts) {
        ClaimsUtil.QueryArg.Builder queryArgsBuilder = ClaimsUtil.QueryArg.newBuilder();

        return Stream.of(parts).map(x -> switch (x) {
            case Integer v -> queryArgsBuilder.setInt(v).build();
            case Long v -> queryArgsBuilder.setInt(v).build();
            case Float v -> queryArgsBuilder.setFloat(v).build();
            case Double v -> queryArgsBuilder.setFloat(v).build();
            case String v -> queryArgsBuilder.setString(v).build();
            case byte[] v -> queryArgsBuilder.setBlob(ByteString.copyFrom(v)).build();
            default -> throw new UnknownQueryArgTypeException();
        }).toList();
    }

    Object makeRequest(Object dat, ClaimsUtil.Sub subject, boolean isMigration) throws HttpBadRequestException, HttpUnauthorizedException, HttpMissingHeaderException, InvalidProtocolBufferException, ProtoPackageErrorException {
        var data = switch (dat) {
            case ClaimsUtil.QueryRequest v -> v;
            case ClaimsUtil.MigrationRequest v -> v;
            default -> throw new UnknownRequestDatTypeException();
        };
        ProtoPackage protoPackage = this.protoManager.encodeProto(data, subject, this.usernamePasswordHash);
        HttpResponse<byte[]> response = this.makeRequest(
                protoPackage,
                isMigration
        );

        // if bad request / unauthorized, don't treat as proto
        if (response.statusCode() == 400) {
            throw new HttpBadRequestException(new String(response.body()));
        }
        if (response.statusCode() == 401) {
            throw new HttpUnauthorizedException(new String(response.body()));
        }

        byte[] body = response.body();
        String signature = response.headers().firstValue("0").orElseThrow(() -> new HttpMissingHeaderException("Missing header 0", "Response signature header missing"));

        return this.handleResponse(body, signature);
    }

    private HttpResponse<byte[]> makeRequest(ProtoPackage protoPackage, boolean isMigration) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            isMigration
                                    ? String.format("%s/m", this.fullAddress)
                                    : this.fullAddress
                    ))
                    .header("Content-Type", "application/protobuf")
                    .header("0", this.usernameHash)
                    .header("1", protoPackage.getSignature())
                    .POST(HttpRequest.BodyPublishers.ofByteArray(protoPackage.getData()))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Object handleResponse(byte[] body, String signature) throws ProtoPackageErrorException, InvalidProtocolBufferException {
        ProtoRequest.Request response = this.protoManager.decodeProto(body, this.usernamePasswordHash, signature);
        ProtoRequest.Claims claims = response.getClaims();
        return switch (claims.getDatCase()) {
            case ProtoRequest.Claims.DatCase.FETCHRESPONSE -> claims.getFetchResponse();
            case ProtoRequest.Claims.DatCase.MUTATIONRESPONSE -> claims.getMutationResponse();
            case ProtoRequest.Claims.DatCase.MIGRATIONRESPONSE -> claims.getMigrationResponse();
            default -> null;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connector connector = (Connector) o;
        return Objects.equals(fullAddress, connector.fullAddress)
                && Objects.equals(usernameHash, connector.usernameHash)
                && Objects.equals(usernamePasswordHash, connector.usernamePasswordHash)
                && Objects.equals(protoManager, connector.protoManager)
                && Objects.equals(objectMapper, connector.objectMapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullAddress, usernameHash, usernamePasswordHash, protoManager, objectMapper);
    }

//    enable this for debug purposes
//    @Override
//    public String toString() {
//        return getClass() + " " + "fullAddress=" + fullAddress + ", usernameHash=" + usernameHash
//                + ", usernamePasswordHash=" + usernamePasswordHash + ", tokenManager=" + tokenManager + ", objectMapper=" + objectMapper;
//    }
}
