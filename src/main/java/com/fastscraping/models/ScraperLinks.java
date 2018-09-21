package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScraperLinks {

    private final String clientId;
    private final String jobId;
    private final List<String> links;

    @JsonCreator
    public ScraperLinks(@JsonProperty("clientId") String clientId,
                        @JsonProperty("jobId") String jobId,
                        @JsonProperty("links") List<String> links) {
        this.clientId = clientId;
        this.jobId = jobId;
        this.links = links;
    }

    public String getClientId() {
        return clientId;
    }

    public String getJobId() {
        return jobId;
    }

    public List<String> getLinks() {
        return links;
    }
}
