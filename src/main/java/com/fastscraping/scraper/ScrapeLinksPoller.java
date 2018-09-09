package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;

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

    private ScrapeLinksPoller(WebpageScraper webpageScraper, ScraperDaoInf scraperDao, int numberOfThreads) {
        this.webpageScraper = webpageScraper;
        this.scraperDao = scraperDao;
        this.executor = Executors.newScheduledThreadPool(numberOfThreads);
    }

    public void addClientJob(final String clientId, final String jobId) {
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
            List<String> polledLinks =  scraperDao.getLinksToScrape(clientId, jobId);
            webpageScraper.scrapeNewLinks(polledLinks, clientId, jobId);
        }
    }

    public static ScrapeLinksPoller getSingletonInstance(WebpageScraper webpageScraper, ScraperDaoInf scraperDao,
                                                         int numberOfThreads) {
        synchronized (SCRAPE_LINKS_POLLER_LOCK) {
            if (sscrapeLinksPoller == null) {
                sscrapeLinksPoller = new ScrapeLinksPoller(webpageScraper, scraperDao, numberOfThreads);
            }
        }
        return sscrapeLinksPoller;
    }

    private static class ScrapeLinksPollerLock {}
}
