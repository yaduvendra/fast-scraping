package com.fastscraping.dao;

import java.util.List;

public interface ScraperDaoInf {
    List<Boolean> saveLinksToScrape(String key, List<String> links);
    List<String> getLinksToScrape(String key);
}
