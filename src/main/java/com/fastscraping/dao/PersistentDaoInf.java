package com.fastscraping.dao;

import com.fastscraping.models.ActionsAndData;
import com.fastscraping.models.ScrapingInformation;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

public interface PersistentDaoInf {
    void addScrapingInforamtion(ScrapingInformation information);

    void addScrapingInforamtion(String jsonDoc, String clientId, String jobId);

    void addLinksToScrape(String clientId, String jobId, List<String> links);

    void getUnscrapedLinksInMemory(String clientId, String jobId);

    List<String> getLinksToScrape(String clientId, String jobId);

    List<Optional<ActionsAndData>> getElementsWithActionsByLink(String link) throws MalformedURLException;

    void updateScrapedTrue(String clientId, String jobId, String job);

    void closeDBConnection();

    boolean addToScrapedLinks(String link, String clientId, String jobId);

    boolean saveSrapedData(String database, String key, String data);
}
