package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DataToExtract {

    private final String storageKeyName;
    private final String selector;
    private final List<String> attributes;
    private final boolean text;
    private final boolean image;
    private final String database;

    @JsonCreator
    public DataToExtract(@JsonProperty("storageKeyName") String storageKeyName,
                         @JsonProperty("selector") String selector,
                         @JsonProperty("attributes") List<String> attributes,
                         @JsonProperty("text") boolean text,
                         @JsonProperty("image") boolean image,
                         @JsonProperty("database") String database) {
        this.storageKeyName = storageKeyName;
        this.selector = selector;
        this.attributes = attributes;
        this.text = text;
        this.image = image;
        this.database = database;
    }

    public String getStorageKeyName() {
        return storageKeyName;
    }

    public String getSelector() {
        return selector;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public boolean isText() {
        return text;
    }

    public String getDatabase() {
        return database;
    }

    public boolean isImage() {
        return image;
    }
}
