package se.rikardbq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.rikardbq.jwt.TokenDecoder;
import se.rikardbq.models.Claims;
import se.rikardbq.models.FetchResponse;
import se.rikardbq.models.QueryRequest;
import se.rikardbq.models.TokenPayload;
import se.rikardbq.temp.SomeDataClass;

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
                    .uri(URI.create("http://localhost:8080/ceb588b4ba47c60042a00cd55646f6c2d30fd224c5013c749a3425b310ed5788"))
                    .header("Content-Type", "application/json")
                    .header("u_", "1160130875fda0812c99c5e3f1a03516471a6370c4f97129b221938eb4763e63")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"payload\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTRVJWRVIiLCJzdWIiOiJGRVRDSCIsImRhdCI6eyJxdWVyeSI6IlNFTEVDVCAqIEZST00gdGVzdGluZ190YWJsZTsiLCJwYXJ0cyI6W119LCJpYXQiOjE3NDA0MjMxMTgsImV4cCI6MTc0MDQyMzE0OH0.HPmWD5LVseb8qyAdsSsquAFiYHlZVSSLDmlbdqh9qlU\"}"))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept((res) -> {
                        System.out.println(res);
                        try {
                            TokenPayload tokenRes = objectMapper.readValue(res, TokenPayload.class);
                            var decoded = TokenDecoder.decodeToken(tokenRes.getPayload());
                            FetchResponse<SomeDataClass> test = objectMapper.readValue(decoded.getClaims().get("dat").toString(), new TypeReference<>() {
                            });
//                            Claims<QueryRequest> claims = objectMapper.readValue(res, new TypeReference<>(){});
//                            QueryRequest req = objectMapper.readValue(claims.getDat().toString(), QueryRequest.class);
                            System.out.println(decoded.getClaims().get("dat").toString());
                            for (SomeDataClass a : test.getData()) {
                                System.out.println(a.getImData());
                            }
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