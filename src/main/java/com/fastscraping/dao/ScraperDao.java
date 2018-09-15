package com.fastscraping.dao;

import com.fastscraping.models.ActionsAndData;
import com.fastscraping.models.ScrapingInformation;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * This DAO is composed of two sub-DAOs.
 * InMemoryDaoInf is used as a cache for faster access.
 * PersistentDaoInf is used as the file based persistent data store.
 *
 * While writing data, approach is to write first in persistent db and then in memory db
 * To read data, read first from in-memory db and then from persistent db (if not found in in-memory db)
 */
public class ScraperDao implements ScraperDaoInf {

    private final InMemoryDaoInf inMemeoryDao;
    private final PersistentDaoInf persistentDao;

    public ScraperDao(InMemoryDaoInf inMemeoryDao, PersistentDaoInf persistentDao) {
        this.inMemeoryDao = inMemeoryDao;
        this.persistentDao = persistentDao;
    }

    /**
     * First store the information in persistent db then in in-memory db
     * @return status for all the link insertions
     */
    @Override
    public List<Boolean> addLinksToScrape(String clientId, String jobId, List<String> links) {
        persistentDao.addLinksToScrape(clientId, jobId, links);
        return inMemeoryDao.addLinksToScrape(clientId, jobId, links);
    }

    /**
     * Get links from in-memory db. If no links found then try persistent db.
     * @return links
     */
    @Override
    public List<String> getLinksToScrape(String clientId, String jobId) {
        List<String> links = inMemeoryDao.getLinksToScrape(clientId, jobId);
        if(links == null || links.size() == 0) {
            return persistentDao.getLinksToScrape(clientId, jobId);
        } else {
            return links;
        }
    }

    @Override
    public void addScrapingInforamtion(ScrapingInformation scrapingInfo) {
        persistentDao.addScrapingInforamtion(scrapingInfo);
        inMemeoryDao.addScrapingInforamtion(scrapingInfo);
    }

    @Override
    public List<Optional<ActionsAndData>> getElementsWithActionsByLink(String link) throws MalformedURLException {
        List<Optional<ActionsAndData>> elementsWithActions = inMemeoryDao.getElementsWithActionsByLink(link);

        if(elementsWithActions.size() == 0) {
            return persistentDao.getElementsWithActionsByLink(link);
        } else{
            return elementsWithActions;
        }
    }

    @Override
    public void closeDBConnection() {
        persistentDao.closeDBConnection();
        inMemeoryDao.closeDBConnection();
    }

    @Override
    public boolean addToScrapedLinks(String link, String clientId, String jobId) {
        return persistentDao.addToScrapedLinks(link, clientId, jobId) && //Add this link to both databases
                inMemeoryDao.addToScrapedLinks(link, clientId, jobId);
    }
}
