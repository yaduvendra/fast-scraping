package com.fastscraping.dao;

import com.fastscraping.models.ActionsAndData;
import com.fastscraping.models.ScrapingInformation;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

public interface PersistentDaoInf {
    void addScrapingInforamtion(ScrapingInformation information);

    void addLinksToScrape(String clientId, String jobId, List<String> links);

    Future<List<String>> getUnscrapedLinks(String clientId, String jobId);

    Future<List<String>> getLinksToScrape(String clientId, String jobId);

    Future<List<ScrapingInformation>> getScrapingInformation(String clientId, String jobId);

    void updateScrapedTrue(String clientId, String jobId, String job);

    void closeDBConnection();

    boolean addToScrapedLinks(String link, String clientId, String jobId);

    boolean addSrapedData(String clientId, String jobId, Map<String, Map<String, Object>> collection);
}
