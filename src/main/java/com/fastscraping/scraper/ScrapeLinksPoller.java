package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScrapeLinksPoller {

    private final ScraperDaoInf scraperDao;

    private final ScheduledExecutorService executor;

    public ScrapeLinksPoller(ScraperDaoInf scraperDao, int numberOfThreads) {
        this.scraperDao = scraperDao;
        this.executor = Executors.newScheduledThreadPool(numberOfThreads);
    }

    public void addClientJob(String clientId, String jobId) {
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
        //TODO: Send these links with client and job information to the client to threadpool managing the scraping work
        }
    }

}
