package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.fastscraping.scraper.ActionExecutor.ActionExecutorBuilder;

public class WebpageScraper {

    private static final SingletonWebpageScraperLock SINGLETON_WEBPAGE_SCRAPER_LOCK = new SingletonWebpageScraperLock();
    private static WebpageScraper singletonWebpageScraper = null;

    private final ExecutorService executorService;

    private final ScraperDaoInf scraperDao;

    private WebpageScraper(ScraperDaoInf scraperDao, int numberOfThreads) {
        this.scraperDao = scraperDao;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
    }

    private WebpageScraper(ScraperDaoInf scraperDao, ExecutorService executorService) {
        this.scraperDao = scraperDao;
        this.executorService = executorService;
    }

    /**
     * The singleton builder of the WebpageScraper
     */
    public static WebpageScraper getSingletonWebpageScraper(ScraperDaoInf scraperDao, int numberOfThreads) {
        synchronized (SINGLETON_WEBPAGE_SCRAPER_LOCK) {
            if (singletonWebpageScraper == null) {
                singletonWebpageScraper = new WebpageScraper(scraperDao, numberOfThreads);
            }
        }
        return singletonWebpageScraper;
    }

    public static WebpageScraper getSingletonWebpageScraper(ScraperDaoInf scraperDao, ExecutorService executorService) {
        synchronized (SINGLETON_WEBPAGE_SCRAPER_LOCK) {
            if (singletonWebpageScraper == null) {
                singletonWebpageScraper = new WebpageScraper(scraperDao, executorService);
            }
        }
        return singletonWebpageScraper;
    }

    public void scrapeNewLinks(List<String> newLinks, String clientId, String jobId) {
        System.out.println("Scraping new links.");
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

            ActionFilter actionFilter = new ActionFilter(scraperDao);

            newLinks.forEach(linkToScrape -> {
                        try {
                            Optional<WebDriver> webDriverOptional = WebDriverKeeper.getWebDriver(clientId, jobId);
                            if (!webDriverOptional.isPresent()) {
                                //TODO: Submit the job to be done at a scheduled interval till no webdriver is free
                            } else {
                                WebDriver driver = webDriverOptional.get();
                                driver.get(linkToScrape);
                                ActionExecutor actionExecutor = new ActionExecutorBuilder().setDriver(driver).build();

                                actionFilter.getActionsByLink(linkToScrape)
                                        .forEach(elementWithAction -> actionExecutor.executeAction(elementWithAction,
                                                scraperDao, linkToScrape, clientId, jobId));

                                WebDriverKeeper.addBackWebDriver(clientId, jobId, driver);
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
    }
}
