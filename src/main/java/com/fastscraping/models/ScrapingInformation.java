package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ScrapingInformation {

    private final String clientId;
    private final String jobId;
    private final int numberOfBrowsers;
    private final List<String> roots;
    private final List<Webpage> webpages;

    @JsonCreator
    public ScrapingInformation(@JsonProperty("clientId") String clientId,
                               @JsonProperty("jobId") String jobId,
                               @JsonProperty("numberOfBrowsers") int numberOfBrowsers,
                               @JsonProperty("roots") List<String> roots,
                               @JsonProperty("webpages") List<Webpage> webpages) {
        this.clientId = clientId;
        this.jobId = jobId;
        this.numberOfBrowsers = numberOfBrowsers;
        this.roots = roots;
        this.webpages = webpages;
    }

    public String getClientId() {
        return clientId;
    }

    public String getJobId() {
        return jobId;
    }

    public int getNumberOfBrowsers() {
        return numberOfBrowsers;
    }

    public List<String> getRoots() {
        return roots;
    }

    public List<Webpage> getWebpages() {
        return webpages;
    }

}
