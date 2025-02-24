package se.rikardbq.temp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SomeDataClass implements Serializable {
    private static long serialVersionUID = 1L;

    private int id;
    @JsonProperty("im_data")
    private String imData;
    @JsonProperty("im_data_also")
    private String imDataAlso;
    @JsonProperty("im_data_too")
    private String imDataToo;

    public void setId(int id) {
        this.id = id;
    }

    public void setImData(String imData) {
        this.imData = imData;
    }

    public void setImDataAlso(String imDataAlso) {
        this.imDataAlso = imDataAlso;
    }

    public void setImDataToo(String imDataToo) {
        this.imDataToo = imDataToo;
    }

    public int getId() {
        return id;
    }

    public String getImData() {
        return imData;
    }

    public String getImDataAlso() {
        return imDataAlso;
    }

    public String getImDataToo() {
        return imDataToo;
    }
}
