package com.fastscraping.scraper;

import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.models.ScrapingInformation;
import org.openqa.selenium.JavascriptExecutor;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebpageScraper {

    private final ExecutorService executor;
    private final SeleniumSetup seleniumSetup;
    private final JavascriptExecutor jsExecutor;
    private final ActionExecutor actionExecutor;
    private final ActionFilter actionFilter;
    private final RedisDao redisDao;

    private WebpageScraper(SeleniumSetup seleniumSetup, RedisDao redisDao,
                           ActionExecutor actionExecutor, ActionFilter actionFilter, int numberOfThreads) {
        this.seleniumSetup = seleniumSetup;
        this.redisDao = redisDao;
        this.actionExecutor = actionExecutor;
        this.actionFilter = actionFilter;
        this.executor = Executors.newFixedThreadPool(numberOfThreads);

        this.jsExecutor = (JavascriptExecutor) (this.seleniumSetup.getWebDriver());
    }

    public void startScraping(ScrapingInformation scrapingInformation) {
        executor.execute(new Worker(scrapingInformation));
    }

    private class Worker implements Runnable {

        private ScrapingInformation scrapingInformation;

        private Worker(ScrapingInformation scrapingInformation) {
            this.scrapingInformation  = scrapingInformation;
        }

        @Override
        public void run() {
            redisDao.indexScrapingInforamtion(scrapingInformation);
            seleniumSetup.getWebDriver().get(linkToScrape);
            try {
                actionFilter.getActionsByLink(linkToScrape).forEach(elementWithAction -> {
                    actionExecutor.executeAction(elementWithAction, redisDao, linkToScrape, "", "");
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            seleniumSetup.getWebDriver().close();
        }
    }

    /**
     * The builder of the WebpageScraper
     */
    public static class WebpageScraperBuilder {
        private SeleniumSetup seleniumSetup;
        private ActionExecutor actionExecutor;
        private ActionFilter actionFilter;
        private RedisDao redisDao;
        private int numberOfThreads;

        public WebpageScraperBuilder() {

        }

        public WebpageScraperBuilder setWebpage(SeleniumSetup seleniumSetup) {
            this.seleniumSetup = seleniumSetup;
            return this;
        }

        public WebpageScraperBuilder setActionExecutor(ActionExecutor actionExecutor) {
            this.actionExecutor = actionExecutor;
            return this;
        }

        public WebpageScraperBuilder setActionFilter(ActionFilter actionFilter) {
            this.actionFilter = actionFilter;
            return this;
        }

        public WebpageScraperBuilder setRedisDao(RedisDao redisDao) {
            this.redisDao = redisDao;
            return this;
        }

        public WebpageScraper build() {
            return new WebpageScraper(
                    this.seleniumSetup,
                    this.redisDao,
                    this.actionExecutor,
                    this.actionFilter,
                    this.numberOfThreads);
        }
    }
}
