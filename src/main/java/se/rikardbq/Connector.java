package se.rikardbq;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import se.rikardbq.jwt.TokenManager;
import se.rikardbq.models.Enums;
import se.rikardbq.models.FetchResponse;
import se.rikardbq.models.MutationResponse;
import se.rikardbq.models.TokenPayload;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class Connector {

    private String fullAddress;
    private String usernameHash;
    private String usernamePasswordHash;

    private TokenManager tokenManager;
    private ObjectMapper objectMapper;

    public Connector() {
    }

    public Connector(String address, String database, String username, String password) {
        this.tokenManager = new TokenManager();
        this.objectMapper = new ObjectMapper();
        String hashedDatabase = DigestUtils.sha256Hex(database);
        String hashedUsername = DigestUtils.sha256Hex(username);
        String hashedUsernamePassword = DigestUtils.sha256Hex(username + password);

        this.fullAddress = String.format("%s/%s", address, hashedDatabase);
        this.usernameHash = hashedUsername;
        this.usernamePasswordHash = hashedUsernamePassword;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public <T> List<T> query(String query, Object... parts) throws JsonProcessingException {
        FetchResponse<T> qRes = makeQuery(query, parts);

        return qRes.getData();
    }

    private <T> FetchResponse<T> makeQuery(String query, Object[] parts) throws JsonProcessingException {
        String response = this.makeRequest(this.createQueryDat(query, parts), Enums.Subject.FETCH, false);

        return objectMapper.readValue(response, new TypeReference<>() {
        });
    }

    public long mutate(String query, Object... parts) throws JsonProcessingException {
        MutationResponse mRes = makeMutation(query, parts);

        return mRes.getRowsAffected();
    }

    private MutationResponse makeMutation(String query, Object[] parts) throws JsonProcessingException {
        String response = this.makeRequest(this.createQueryDat(query, parts), Enums.Subject.MUTATE, false);

        return objectMapper.readValue(response, MutationResponse.class);
    }

    public String makeRequest(Map<String, Object> dat, Enums.Subject subject, boolean isMigration) throws JsonProcessingException {
        String token = tokenManager.encodeToken(
                dat,
                subject,
                this.usernamePasswordHash
        );
        String response = this.makeRequest(new TokenPayload(token, null), objectMapper, isMigration);

        return this.handleResponse(response);
    }

    private String handleResponse(String response) throws JsonProcessingException {
        return this.handleResponse(response, objectMapper, tokenManager);
    }

    private String makeRequest(TokenPayload requestBody, ObjectMapper objectMapper, boolean isMigration) throws JsonProcessingException {
        String reqBody = objectMapper.writeValueAsString(requestBody);
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(isMigration ? String.format("%s/m", this.fullAddress) : this.fullAddress))
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

    private String handleResponse(String response, ObjectMapper objectMapper, TokenManager tokenManager) throws JsonProcessingException {
        TokenPayload resToken = objectMapper.readValue(response, TokenPayload.class);
        DecodedJWT decodedJWT = tokenManager.decodeToken(resToken.getPayload(), this.usernamePasswordHash);
        Claim datClaim = decodedJWT.getClaims().get("dat");

        return datClaim.toString();
    }

    private Map<String, Object> createQueryDat(String query, Object[] parts) {
        return new TokenManager.DatBuilder()
                .withField("query", query)
                .withField("parts", List.of(parts))
                .build();
    }
}
