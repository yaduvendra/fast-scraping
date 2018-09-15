package com.fastscraping.dao;

import com.fastscraping.models.ScrapingInformation;

import java.util.List;
import java.util.concurrent.Future;

public interface PersistentDaoInf {
    Future<List<Boolean>> addScrapingInforamtion(ScrapingInformation information);

    void addScrapingInforamtion(String jsonDoc, String clientId, String jobId);

    void addLinksToScrape(String clientId, String jobId, List<String> links);

    void getUnscrapedLinksInMemory(String clientId, String jobId);

    List<String> getLinksToScrape(String clientId, String jobId);

    void updateScrapedTrue(String clientId, String jobId, String job);

    void closeDBConnection();
}
