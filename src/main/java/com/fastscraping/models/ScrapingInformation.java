package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ScrapingInformation {

    private final List<String> roots;
    private final List<Webpage> webpages;

    @JsonCreator
    public ScrapingInformation(@JsonProperty("roots") List<String> roots,
                               @JsonProperty("webpages") List<Webpage> webpages) {
        this.roots = roots;
        this.webpages = webpages;
    }

}
