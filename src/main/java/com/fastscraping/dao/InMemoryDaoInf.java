package com.fastscraping.dao;

import com.fastscraping.models.ElementWithActions;
import com.fastscraping.models.ScrapingInformation;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

public interface InMemoryDaoInf {
    List<Boolean> addLinksToScrape(final String clientId, final String jobId, List<String> links);

    List<String> getLinksToScrape(final String clientId, final String jobId);

    int getNumberOfBrowser(final String clientId, final String jobId);

    void addScrapingInforamtion(ScrapingInformation scrapingInfo);

    List<Optional<ElementWithActions>> getElementsWithActionsByLink(String link) throws MalformedURLException;

    void closeDBConnection();
}
