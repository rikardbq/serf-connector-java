package se.rikardbq;

import se.rikardbq.connector.Connector;
import se.rikardbq.connector.Migrator;
import se.rikardbq.temp.SomeDataClass;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
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
//            migrator.run(conn);

//            var document = DocumentBuilderFactory
//                    .newInstance()
//                    .newDocumentBuilder()
//                    .newDocument();
//
//            var testingParent = document.createElement("TestingParent");
//
//            testingParent.setTextContent("hello");
//            document.appendChild(testingParent);
//            var src = new DOMSource(document);
//            var str = new StreamResult(System.out);
//
//            var trs = TransformerFactory.newInstance().newTransformer();
//            trs.transform(src, str);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}