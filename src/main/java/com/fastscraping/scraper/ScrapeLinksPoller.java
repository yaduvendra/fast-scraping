package com.fastscraping.scraper;

import com.fastscraping.dao.InMemoryDaoInf;
import com.fastscraping.dao.PersistentDaoInf;
import com.fastscraping.util.ScraperThreadPools;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScrapeLinksPoller {

    private final static ScrapeLinksPollerLock SCRAPE_LINKS_POLLER_LOCK = new ScrapeLinksPollerLock();
    private static ScrapeLinksPoller scrapeLinksPoller = null;

    private final WebpageScraper webpageScraper;
    private final InMemoryDaoInf inMemoryDb;
    private final ScheduledExecutorService executor;
    private final PersistentDaoInf persistentDb;

    private ScrapeLinksPoller(WebpageScraper webpageScraper, InMemoryDaoInf inMemoryDb, PersistentDaoInf persistentDb) {
        this.webpageScraper = webpageScraper;
        this.inMemoryDb = inMemoryDb;
        this.executor = ScraperThreadPools.scheduledExecutor;
        this.persistentDb = persistentDb;
    }

    public static ScrapeLinksPoller getSingletonInstance(WebpageScraper webpageScraper, InMemoryDaoInf scraperDao,
                                                         PersistentDaoInf persistentDb) {
        synchronized (SCRAPE_LINKS_POLLER_LOCK) {
            if (scrapeLinksPoller == null) {
                scrapeLinksPoller = new ScrapeLinksPoller(webpageScraper, scraperDao, persistentDb);
            }
        }
        return scrapeLinksPoller;
    }

    public void addClientJob(final String clientId, final String jobId) {
        System.out.println("Adding the client's scheduled job. Initial Delay: " + 5 + " seconds." + " Period: " + 2 + " seconds");
        executor.scheduleAtFixedRate(new PollWorker(clientId, jobId), 5L, 2L, TimeUnit.SECONDS);
    }

    private static class ScrapeLinksPollerLock {
    }

    private class PollWorker implements Runnable {
        private final String clientId;
        private final String jobId;

        private PollWorker(String clientId, String jobId) {
            this.clientId = clientId;
            this.jobId = jobId;
        }

        @Override
        public void run() {
            System.out.println("Running the scheduled job.");
            List<String> polledLinks = inMemoryDb.getLinksToScrape(clientId, jobId);

            if (polledLinks.size() == 0) {
                persistentDb.getUnscrapedLinksInMemory(clientId, jobId); //This will put the links in the in-memory db
                return;
            }

            System.out.println("Received the links to poll. Total are - " + polledLinks.size());
            webpageScraper.scrapeNewLinks(polledLinks, clientId, jobId);
            System.out.println("Sent the links to the Scraper to be scraped.");
        }
    }
}
