package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.models.ActionsAndData;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionFilter {

    private final ScraperDaoInf scraperDao;

    public ActionFilter(ScraperDaoInf scraperDao) {
        this.scraperDao = scraperDao;
    }

    public List<ActionsAndData> getActionsAndDataByLink(String link) throws MalformedURLException {

        return scraperDao.getActionsAndDataByLink(link)
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
