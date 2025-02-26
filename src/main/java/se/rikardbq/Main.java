package se.rikardbq;

import com.fasterxml.jackson.core.JsonProcessingException;
import se.rikardbq.connector.Connector;
import se.rikardbq.connector.Migrator;
import se.rikardbq.temp.SomeDataClass;

import java.util.List;

public class Main {
    static Connector conn = new Connector(
            "http://localhost:8080",
            "test666",
            "test_user",
            "test_pass"
    );
    static Migrator migrator = new Migrator("./migrations");

    static void generateToken() {
//        try (HttpClient client = HttpClient.newHttpClient()) {
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost:8080/ceb588b4ba47c60042a00cd55646f6c2d30fd224c5013c749a3425b310ed5788"))
//                    .header("Content-Type", "application/json")
//                    .header("u_", "1160130875fda0812c99c5e3f1a03516471a6370c4f97129b221938eb4763e63")
//                    .POST(HttpRequest.BodyPublishers.ofString("{\"payload\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTRVJWRVIiLCJzdWIiOiJGRVRDSCIsImRhdCI6eyJxdWVyeSI6IlNFTEVDVCAqIEZST00gdGVzdGluZ190YWJsZTsiLCJwYXJ0cyI6W119LCJpYXQiOjE3NDA0MjMxMTgsImV4cCI6MTc0MDQyMzE0OH0.HPmWD5LVseb8qyAdsSsquAFiYHlZVSSLDmlbdqh9qlU\"}"))
//                    .build();
//
//            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .thenApply(HttpResponse::body)
//                    .thenAccept((res) -> {
//                        System.out.println(res);
//                        try {
//                            TokenPayload tokenRes = objectMapper.readValue(res, TokenPayload.class);
//                            var decoded = TokenManager(tokenRes.getPayload());
//                            FetchResponse<SomeDataClass> test = objectMapper.readValue(decoded.getClaims().get("dat").toString(), new TypeReference<>() {
//                            });
//                            Connector conn = new Connector();
//                            List<SomeDataClass> asd = conn.query();
////                            Claims<QueryRequest> claims = objectMapper.readValue(res, new TypeReference<>(){});
////                            QueryRequest req = objectMapper.readValue(claims.getDat().toString(), QueryRequest.class);
//                            System.out.println(decoded.getClaims().get("dat").toString());
//                            for (SomeDataClass a : test.getData()) {
//                                System.out.println(a.getImData());
//                            }
//                        } catch (JsonProcessingException e) {
//                            throw new RuntimeException(e);
//                        }
////                        System.out.println(tokenRes.toString());
//                    })
//                    .join();
//        }


    }

    public static void main(String[] args) {

//        for (int i = 1; i<100; i++) {
//            if (i == 50) {
//                break;
//            }
//
//            System.out.println(i);
//        }
        try {
            List<SomeDataClass> data = conn.query("SELECT * FROM testing_table;");
            long rowsAffected = conn.mutate("INSERT INTO testing_table(im_data, im_data_also, im_data_too) VALUES(?, ?, ?)", "teeeeeeee8888888", "heeeeeeeeee", "wwaaaaaaaaaaa");
            System.out.println("DATA=====" + " " + data);
            System.out.println(rowsAffected);
            migrator.run(conn);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}