package se.rikardbq.connector;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.digest.DigestUtils;
import se.rikardbq.exception.ProtoPackageVerifyErrorException;
import se.rikardbq.exception.TokenPayloadErrorException;
import se.rikardbq.exception.UnknownQueryArgTypeException;
import se.rikardbq.jwt.TokenManager;
import se.rikardbq.models.Enums;
import se.rikardbq.models.FetchResponse;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.models.TokenPayload;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Connector {

    private final String fullAddress;
    private final String usernameHash;
    private final String usernamePasswordHash;

    private final TokenManager tokenManager;
    private final ProtoManager protoManager;
    private final ObjectMapper objectMapper;

    public Connector(String address, String database, String username, String password) {
        this.tokenManager = new TokenManager();
        this.protoManager = new ProtoManager();
        this.objectMapper = new ObjectMapper();
        String hashedDatabase = DigestUtils.sha256Hex(database);
        String hashedUsername = DigestUtils.sha256Hex(username);
        String hashedUsernamePassword = DigestUtils.sha256Hex(username + password);

        this.fullAddress = String.format("%s/%s", address, hashedDatabase);
        this.usernameHash = hashedUsername;
        this.usernamePasswordHash = hashedUsernamePassword;
    }

    public <T> List<T> query(Class<T> valueType, String query, Object... parts) throws JsonProcessingException, TokenPayloadErrorException {
        FetchResponse<T> qRes = makeQuery(query, parts, valueType);

        return qRes.getData();
    }

    public <T> List<T> query_2(Class<T> valueType, String query, Object... parts) throws Exception {
        return makeQuery_2(query, parts, valueType);
    }

    private <T> FetchResponse<T> makeQuery(String query, Object[] parts, Class<T> valueType) throws JsonProcessingException, TokenPayloadErrorException {
        String response = this.makeRequest(
                this.createQueryDat(query, parts),
                Enums.Subject.FETCH,
                false
        );

        return this.objectMapper.readValue(response, this.objectMapper.getTypeFactory().constructParametricType(FetchResponse.class, valueType));
    }

    private <T> List<T> makeQuery_2(String query, Object[] parts, Class<T> valueType) throws Exception {
        ClaimsUtil.QueryRequest.Builder queryRequestBuilder = ClaimsUtil.QueryRequest.newBuilder()
                .setQuery(query)
                .addAllParts(this.mapPartsToQueryArgs(parts));

        ClaimsUtil.FetchResponse response = (ClaimsUtil.FetchResponse) this.makeRequest_2(
                queryRequestBuilder.build(),
                ClaimsUtil.Sub.FETCH,
                false
        );

        return this.objectMapper.readValue(
                response.getData().toByteArray(),
                this.objectMapper.getTypeFactory().constructParametricType(List.class, valueType)
        );
    }

    public long mutate(String query, Object... parts) throws JsonProcessingException, TokenPayloadErrorException {
        MutationResponse mRes = makeMutation(query, parts);

        return mRes.getRowsAffected();
    }

    private MutationResponse makeMutation(String query, Object[] parts) throws JsonProcessingException, TokenPayloadErrorException {
        String response = this.makeRequest(
                this.createQueryDat(query, parts),
                Enums.Subject.MUTATE,
                false
        );

        return this.objectMapper.readValue(response, MutationResponse.class);
    }

    String makeRequest(Map<String, Object> dat, Enums.Subject subject, boolean isMigration) throws JsonProcessingException, TokenPayloadErrorException {
        String token = this.tokenManager.encodeToken(
                dat,
                subject,
                this.usernamePasswordHash
        );
        String response = this.makeRequest(
                new TokenPayload(token, null),
                isMigration
        );

        return this.handleResponse(response);
    }

    private String makeRequest(TokenPayload requestBody, boolean isMigration) throws JsonProcessingException {
        String reqBody = this.objectMapper.writeValueAsString(requestBody);
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            isMigration
                                    ? String.format("%s/m", this.fullAddress)
                                    : this.fullAddress
                    ))
                    .header("Content-Type", "application/json")
                    .header("u_", this.usernameHash)
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());

            return res.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleResponse(String response) throws JsonProcessingException, TokenPayloadErrorException {
        TokenPayload resToken = this.objectMapper.readValue(response, TokenPayload.class);
        if (resToken.getError() != null) {
            throw new TokenPayloadErrorException(resToken.getError());
        }
        DecodedJWT decodedJWT = this.tokenManager.decodeToken(resToken.getPayload(), this.usernamePasswordHash);
        Claim datClaim = decodedJWT.getClaims().get("dat");

        return datClaim.toString();
    }

    private Map<String, Object> createQueryDat(String query, Object[] parts) {
        return Map.ofEntries(
                Map.entry("query", query),
                Map.entry("parts", List.of(parts))
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

    Object makeRequest_2(ClaimsUtil.QueryRequest dat, ClaimsUtil.Sub subject, boolean isMigration) throws Exception {
        ProtoPackage protoPackage = this.protoManager.encodeProto(dat, subject, this.usernamePasswordHash);//this.usernamePasswordHash);
        HttpResponse<byte[]> response = this.makeRequest_2(
                protoPackage,
                isMigration
        );

        byte[] body = response.body();
        String signature = response.headers().firstValue("0").orElseThrow(() -> new Exception("signature header missing error"));

        return this.handleResponse_2(body, signature);
    }

    private HttpResponse<byte[]> makeRequest_2(ProtoPackage protoPackage, boolean isMigration) throws JsonProcessingException {
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

    private Object handleResponse_2(byte[] body, String signature) throws ProtoPackageVerifyErrorException, InvalidProtocolBufferException {
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
                && Objects.equals(tokenManager, connector.tokenManager)
                && Objects.equals(objectMapper, connector.objectMapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullAddress, usernameHash, usernamePasswordHash, tokenManager, objectMapper);
    }

//    enable this for debug purposes
//    @Override
//    public String toString() {
//        return getClass() + " " + "fullAddress=" + fullAddress + ", usernameHash=" + usernameHash
//                + ", usernamePasswordHash=" + usernamePasswordHash + ", tokenManager=" + tokenManager + ", objectMapper=" + objectMapper;
//    }
}
