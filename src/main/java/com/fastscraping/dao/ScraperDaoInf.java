package com.fastscraping.dao;

import com.fastscraping.models.ActionsAndData;
import com.fastscraping.models.ScrapingInformation;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

public interface ScraperDaoInf {

    List<Boolean> addLinksToScrape(final String clientId, final String jobId, List<String> links);


    List<String> getLinksToScrape(final String clientId, final String jobId);

    void addScrapingInforamtion(ScrapingInformation scrapingInfo);

    List<Optional<ActionsAndData>> getElementsWithActionsByLink(String link) throws MalformedURLException;

    void closeDBConnection();

    boolean addToScrapedLinks(final String link, final String clientId, final String jobId);

    boolean addScrapedData(final String database, final String dataKey, final String data);
}
