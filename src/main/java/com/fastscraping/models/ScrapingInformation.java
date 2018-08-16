package com.fastscraping.models;

import java.util.List;

public class ScrapingInformation {

    private final List<String> roots;
    private final List<Webpage> webpages;

    public ScrapingInformation(List<String> roots, List<Webpage> webpages) {
        this.roots = roots;
        this.webpages = webpages;
    }

}
