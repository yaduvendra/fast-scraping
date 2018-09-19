package com.fastscraping.dao;

import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.models.WebpageDetails;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * This DAO is composed of two sub-DAOs.
 * InMemoryDaoInf is used as a cache for faster access.
 * PersistentDaoInf is used as the file based persistent data store.
 * <p>
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
     *
     * @return status for all the link insertions
     */
    @Override
    public List<Boolean> addLinksToScrape(String clientId, String jobId, List<String> links) {
        persistentDao.addLinksToScrape(clientId, jobId, links);
        return inMemeoryDao.addLinksToScrape(clientId, jobId, links);
    }

    /**
     * Get links from in-memory db. If no links found then try persistent db.
     *
     * @return links
     */
    @Override
    public List<String> getLinksToScrape(String clientId, String jobId) {
        List<String> links = inMemeoryDao.getLinksToScrape(clientId, jobId);
        if (links == null || links.size() == 0) {
            /** No new links found in memory */
            try {
                System.out.println("Links not found in memory. Trying the persist database.");
                return persistentDao.getLinksToScrape(clientId, jobId).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.out.println("Error while trying to get data from Persist Data. " + e.getMessage());
                e.printStackTrace();
                return new LinkedList<>();
            }
        } else {
            /** links found in memory */
            return links;
        }
    }

    @Override
    public void addScrapingInforamtion(ScrapingInformation scrapingInfo) {
        persistentDao.addScrapingInforamtion(scrapingInfo);
        inMemeoryDao.addScrapingInforamtion(scrapingInfo);
    }

    @Override
    public List<WebpageDetails> getWebpageDetails(String clientId, String jobId) {
            System.out.println("Getting the WebpageDetails.");
        try {
            return persistentDao.getScrapingInformation(clientId, jobId).get(2, TimeUnit.SECONDS)
                    .stream()
                    .flatMap(scrapingInformation -> scrapingInformation.getWebpageDetails().stream())
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    @Override
    public void closeDBConnections() {
        persistentDao.closeDBConnection();
        inMemeoryDao.closeDBConnection();
    }

    @Override
    public boolean addToScrapedLinks(String link, String clientId, String jobId) {
        return persistentDao.addToScrapedLinks(link, clientId, jobId) && //Add this link to both databases
                inMemeoryDao.addToScrapedLinks(link, clientId, jobId);
    }

    @Override
    public boolean addScrapedData(String clientId, String jobId, Map<String, Map<String, Object>> collection) {
        return persistentDao.addSrapedData(clientId, jobId, collection);
    }

    public List<Boolean> getUnscrapedLinksInMemory(String clientId, String jobId) {
        try {
            List<String> linksFromPersistDB = persistentDao.getUnscrapedLinks(clientId, jobId)
                    .get(2, TimeUnit.SECONDS);
            if (linksFromPersistDB.size() > 0) {
                return inMemeoryDao.addLinksToScrape(clientId, jobId, linksFromPersistDB);
            } else {
                return new LinkedList<>();
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("Couldn't fetch the links from the DB. Reason: " + e.getMessage());
            e.printStackTrace();
            return new LinkedList<>();
        }
    }
}
