package com.fastscraping.dao;

import com.fastscraping.models.ActionsAndData;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.models.WebpageDetails;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ScraperDaoInf {

    List<Boolean> addLinksToScrape(final String clientId, final String jobId, List<String> links);


    List<String> getLinksToScrape(final String clientId, final String jobId);

    void addScrapingInforamtion(ScrapingInformation scrapingInfo);

    List<WebpageDetails> getWebpageDetails(String clientId, String jobId);

    void closeDBConnections();

    boolean addToScrapedLinks(final String link, final String clientId, final String jobId);

    boolean addScrapedData(String clientId, String jobId, final Map<String, Map<String, Object>> collection);

    List<Boolean> getUnscrapedLinksInMemory(String clientId, String jobId);
}
