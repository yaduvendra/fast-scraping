package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.fastscraping.scraper.ActionExecutor.ActionExecutorBuilder;
import static com.fastscraping.util.ScraperThreadPools.fixedThreadPoolExecutor;
import static com.fastscraping.util.ScraperThreadPools.scheduledExecutor;

public class WebpageScraper {

    private static final SingletonWebpageScraperLock SINGLETON_WEBPAGE_SCRAPER_LOCK = new SingletonWebpageScraperLock();
    private static WebpageScraper singletonWebpageScraper = null;

    private final ExecutorService executorService;

    private final ScraperDaoInf scraperDaoInf;

    private WebpageScraper(ScraperDaoInf scraperDaoInf) {
        this.scraperDaoInf = scraperDaoInf;
        this.executorService = fixedThreadPoolExecutor;
    }

    private WebpageScraper(ScraperDaoInf scraperDaoInf, ExecutorService executorService) {
        this.scraperDaoInf = scraperDaoInf;
        this.executorService = executorService;
    }

    /**
     * The singleton builder of the WebpageScraper
     */
    public static WebpageScraper getSingletonWebpageScraper(ScraperDaoInf scraperDao) {
        synchronized (SINGLETON_WEBPAGE_SCRAPER_LOCK) {
            if (singletonWebpageScraper == null) {
                singletonWebpageScraper = new WebpageScraper(scraperDao);
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

            ActionFilter actionFilter = new ActionFilter(scraperDaoInf);

            LinkedList<String> unscrapedLinks = new LinkedList<>();

            newLinks.forEach(linkToScrape -> {
                        Optional<WebDriver> webDriverOptional = WebDriverKeeper.getWebDriver(clientId, jobId);
                        if (!webDriverOptional.isPresent()) {
                            unscrapedLinks.add(linkToScrape);
                        } else {
                            executorService.execute(() -> {
                                WebDriver driver = webDriverOptional.get();

                                try {
                                    driver.get(linkToScrape);
                                    ActionExecutor actionExecutor = new ActionExecutorBuilder().setDriver(driver).build();

                                    actionFilter.getActionsByLink(linkToScrape).forEach(elementWithAction ->
                                            actionExecutor.executeAction(elementWithAction, scraperDaoInf, linkToScrape,
                                                    clientId, jobId));
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } finally {
                                    scraperDaoInf.addToScrapedLinks(linkToScrape, clientId, jobId);
                                    WebDriverKeeper.addBackWebDriver(clientId, jobId, driver);
                                }
                            });
                        }
                    }
            );

            /** The links which are left because of unavailability of drivers,
             *  will be tried for scraping after 2 seconds */
            if (unscrapedLinks.size() > 0) {
                scheduledExecutor.schedule(new Worker(unscrapedLinks, clientId, jobId), 2, TimeUnit.SECONDS);
            }
        }
    }

}
