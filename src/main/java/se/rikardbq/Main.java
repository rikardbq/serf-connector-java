package se.rikardbq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.rikardbq.models.Claims;
import se.rikardbq.models.QueryRequest;
import se.rikardbq.models.TokenPayload;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    static String token = "";
    static ObjectMapper objectMapper = new ObjectMapper();

    static void setToken(String token) {
        Main.token = token;
    }

    static void generateToken() {
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/jwt/decode_token"))
                    .header("Content-Type", "application/json")
                    .header("u_", "1160130875fda0812c99c5e3f1a03516471a6370c4f97129b221938eb4763e63")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"payload\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTRVJWRVIiLCJzdWIiOiJGRVRDSCIsImRhdCI6eyJxdWVyeSI6IlNFTEVDVCAqIEZST00gdGVzdGluZ190YWJsZTsiLCJwYXJ0cyI6W119LCJpYXQiOjE3NDA0MDAxNzAsImV4cCI6MTc0MDQwMDIwMH0.ftQwTY08WJC63hbEH-cOO8ii6rFkFxRpJ7oZbyPz498\"}"))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept((res) -> {
                        System.out.println(res);
                        TokenPayload tokenRes = null;
                        try {
//                            tokenRes = objectMapper.readValue(res, TokenPayload.class);
                            Claims<QueryRequest> claims = objectMapper.readValue(res, new TypeReference<>(){});
//                            QueryRequest req = objectMapper.readValue(claims.getDat().toString(), QueryRequest.class);
                            System.out.println(claims);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                        System.out.println(tokenRes.toString());
                    })
                    .join();
        }


    }

    public static void main(String[] args) {
        generateToken();
//        System.out.println(token);

//        if (!token.isEmpty()) {
//            try (HttpClient client = HttpClient.newHttpClient()) {
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(URI.create("http://localhost:8080/jwt/decode_token"))
//                        .header("Content-Type", "application/json")
//                        .header("u_", "7ab81d3d2fa012bc03f0e9b58f257b630815517f39fb1d7d4c5d6a70b0cf7f33")
//                        .POST(HttpRequest.BodyPublishers.ofString("{\"payload\": \"" + token + "\"}"))
//                        .build();
//
//                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                        .thenApply(HttpResponse::body)
//                        .thenAccept(System.out::println)
//                        .join();
//            }
//        }
    }
}