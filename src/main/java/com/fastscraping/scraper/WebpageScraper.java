package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;
import org.openqa.selenium.JavascriptExecutor;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebpageScraper {

    private static final SingletonWebpageScraperLock SINGLETON_WEBPAGE_SCRAPER_LOCK = new SingletonWebpageScraperLock();
    private static WebpageScraper singletonWebpageScraper = null;

    private final ExecutorService executorService;

    private final SeleniumSetup seleniumSetup;
    private final JavascriptExecutor jsExecutor;
    private final ActionExecutor actionExecutor;
    private final ActionFilter actionFilter;
    private final ScraperDaoInf scraperDao;

    private WebpageScraper(SeleniumSetup seleniumSetup, ScraperDaoInf scraperDao,
                           ActionExecutor actionExecutor, ActionFilter actionFilter, int numberOfThreads) {

        this.seleniumSetup = seleniumSetup;
        this.scraperDao = scraperDao;
        this.actionExecutor = actionExecutor;
        this.actionFilter = actionFilter;

        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.jsExecutor = (JavascriptExecutor) (this.seleniumSetup.getWebDriver());
    }

    private WebpageScraper(SeleniumSetup seleniumSetup, ScraperDaoInf scraperDao, ActionExecutor actionExecutor,
                           ActionFilter actionFilter, ExecutorService executorService) {

        this.seleniumSetup = seleniumSetup;
        this.scraperDao = scraperDao;
        this.actionExecutor = actionExecutor;
        this.actionFilter = actionFilter;

        this.executorService = executorService;
        this.jsExecutor = (JavascriptExecutor) (this.seleniumSetup.getWebDriver());
    }

    /**
     * The singleton builder of the WebpageScraper
     */
    public static WebpageScraper getSingletonWebpageScraper(SeleniumSetup seleniumSetup, ScraperDaoInf scraperDao,
                                                            ActionExecutor actionExecutor, ActionFilter actionFilter,
                                                            int numberOfThreads) {
        synchronized (SINGLETON_WEBPAGE_SCRAPER_LOCK) {
            if (singletonWebpageScraper == null) {
                singletonWebpageScraper = new WebpageScraper(seleniumSetup, scraperDao, actionExecutor, actionFilter,
                        numberOfThreads);
            }
        }
        return singletonWebpageScraper;
    }

    public static WebpageScraper getSingletonWebpageScraper(SeleniumSetup seleniumSetup, ScraperDaoInf scraperDao,
                                                            ActionExecutor actionExecutor, ActionFilter actionFilter,
                                                            ExecutorService executorService) {
        synchronized (SINGLETON_WEBPAGE_SCRAPER_LOCK) {
            if (singletonWebpageScraper == null) {
                singletonWebpageScraper = new WebpageScraper(seleniumSetup, scraperDao, actionExecutor, actionFilter,
                        executorService);
            }
        }
        return singletonWebpageScraper;
    }

    public void scrapeNewLinks(List<String> newLinks, String clientId, String jobId) {
        executorService.execute(new Worker(newLinks, clientId, jobId));
    }

    private static class SingletonWebpageScraperLock {
    }

    private class Worker implements Runnable {

        private final List<String> newLinks;
        private final String clientId;
        private final String jobId;

        private Worker(List<String> newLinks, String clientId, String jobId) {
            this.newLinks = newLinks;
            this.clientId = clientId;
            this.jobId = jobId;
        }

        @Override
        public void run() {

            System.out.println("Scraping the links for client - " + clientId + ", jobId - " + jobId);

            newLinks.forEach(linkToScrape -> {
                        try {
                            seleniumSetup.getWebDriver().get(linkToScrape);

                            actionFilter.getActionsByLink(linkToScrape)
                                    .forEach(elementWithAction -> actionExecutor.executeAction(elementWithAction,
                                            scraperDao, linkToScrape, clientId, jobId));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        seleniumSetup.getWebDriver().close();
                    }
            );
        }
    }
}
