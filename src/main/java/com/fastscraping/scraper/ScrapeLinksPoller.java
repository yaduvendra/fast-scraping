package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.util.ScraperThreadPools;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScrapeLinksPoller {

    private final static ScrapeLinksPollerLock SCRAPE_LINKS_POLLER_LOCK = new ScrapeLinksPollerLock();
    private static ScrapeLinksPoller sscrapeLinksPoller = null;

    private final WebpageScraper webpageScraper;
    private final ScraperDaoInf scraperDao;
    private final ScheduledExecutorService executor;

    private ScrapeLinksPoller(WebpageScraper webpageScraper, ScraperDaoInf scraperDao) {
        this.webpageScraper = webpageScraper;
        this.scraperDao = scraperDao;
        this.executor = ScraperThreadPools.scheduledExecutor;
    }

    public void addClientJob(final String clientId, final String jobId) {
        System.out.println("Adding the client's scheduled job. Initial Delay: " + 5 + " seconds." + " Period: " + 2 + " seconds");
        executor.scheduleAtFixedRate(new PollWorker(clientId, jobId), 5L, 2L, TimeUnit.SECONDS);
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
            List<String> polledLinks =  scraperDao.getLinksToScrape(clientId, jobId);
            System.out.println("Received the links to poll. Total are - " + polledLinks.size());
            webpageScraper.scrapeNewLinks(polledLinks, clientId, jobId);
            System.out.println("Sent the links to the Scraper to be scraped.");
        }
    }

    public static ScrapeLinksPoller getSingletonInstance(WebpageScraper webpageScraper, ScraperDaoInf scraperDao) {
        synchronized (SCRAPE_LINKS_POLLER_LOCK) {
            if (sscrapeLinksPoller == null) {
                sscrapeLinksPoller = new ScrapeLinksPoller(webpageScraper, scraperDao);
            }
        }
        return sscrapeLinksPoller;
    }

    private static class ScrapeLinksPollerLock {}
}
