package se.rikardbq;

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


    public static void main(String[] args) {
        try {
            List<SomeDataClass> data = conn.query("SELECT * FROM testing_table;");
            long rowsAffected = conn.mutate("INSERT INTO testing_table(im_data, im_data_also, im_data_too) VALUES(?, ?, ?)", "teeeeeeee8888888", "heeeeeeeeee", "wwaaaaaaaaaaa");
            System.out.println("DATA===== " + data);
            System.out.println("DATA_ROWS_AFFECTED===== " + rowsAffected);
            migrator.run(conn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}