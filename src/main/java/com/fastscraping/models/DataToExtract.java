package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DataToExtract {

    private final String collection;
    private final String storageKeyName;
    private final String selector;
    private final List<String> attributes;
    private final boolean text;
    private final boolean image;

    /**
     * The data to be extracted from an Element in the DOM. This element also becomes the parent of all other children.
     * The parent element might have multiple children element and for each child a DataToExtract must be defined
     * separately.
     *
     * @param storageKeyName Key for the single scraped value (text/image path). Given by user.
     * @param selector Child selector from which text or image is to be scraped
     * @param attributes The attributes of the parent selector
     * @param text The text inside the parent element and all its children. It scraped and mapped
     *            to the @storageKeyName
     * @param image The image inside the parent element. It's downloaded and it's location on disk is mapped
     *             to the @storageKeyName
     * @param collection The name of the collection (or table in RDBMS) to which this scraped data will belong
     */
    @JsonCreator
    public DataToExtract(@JsonProperty("storageKeyName") String storageKeyName,
                         @JsonProperty("selector") String selector,
                         @JsonProperty("attributes") List<String> attributes,
                         @JsonProperty("text") boolean text,
                         @JsonProperty("image") boolean image,
                         @JsonProperty("collection") String collection) {
        this.storageKeyName = storageKeyName;
        this.selector = selector;
        this.attributes = attributes;
        this.text = text;
        this.image = image;
        this.collection = collection;
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

    public String getCollection() {
        return collection;
    }

    public boolean isImage() {
        return image;
    }
}
