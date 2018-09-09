package com.fastscraping.dao;

import com.fastscraping.models.ScrapingInformation;

import java.util.List;

public interface ScraperDaoInf {
    List<Boolean> saveLinksToScrape(final String clientId, final String jobId, List<String> links);
    List<String> getLinksToScrape(final String clientId, final String jobId);
    int getNumberOfBrowser(final String clientId, final String jobId);
    public void indexScrapingInforamtion(ScrapingInformation scrapingInfo);
}
