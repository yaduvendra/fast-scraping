package com.fastscraping.scraper;

import com.fastscraping.dao.InMemoryDaoInf;
import com.fastscraping.models.ElementWithActions;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionFilter {

    private final InMemoryDaoInf scraperDao;

    public ActionFilter(InMemoryDaoInf scraperDao) {
        this.scraperDao = scraperDao;
    }

    public List<ElementWithActions> getActionsByLink(String link) throws MalformedURLException {

        return scraperDao.getElementsWithActionsByLink(link)
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
