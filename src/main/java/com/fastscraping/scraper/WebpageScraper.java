package com.fastscraping.scraper;

import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.models.ScrapingInformation;
import org.openqa.selenium.JavascriptExecutor;

import java.net.MalformedURLException;

public class WebpageScraper {

    private final SeleniumSetup seleniumSetup;
    private final String linkToScrape;
    private final JavascriptExecutor jsExecutor;
    private final ActionExecutor actionExecutor;
    private final ActionFilter actionFilter;
    private final RedisDao redisDao;

    private WebpageScraper(SeleniumSetup seleniumSetup, String linkToScrape, RedisDao redisDao,
                           ActionExecutor actionExecutor, ActionFilter actionFilter) {
        this.seleniumSetup = seleniumSetup;
        this.linkToScrape = linkToScrape;
        this.redisDao = redisDao;
        this.actionExecutor = actionExecutor;
        this.actionFilter = actionFilter;

        this.jsExecutor = (JavascriptExecutor) (this.seleniumSetup.getWebDriver());
    }

    public void startScraping(ScrapingInformation scrapingInformation) throws MalformedURLException {

        redisDao.indexScrapingInforamtion(scrapingInformation);

        seleniumSetup.getWebDriver().get(linkToScrape);

        System.out.println("The browser opened.......");

        actionFilter.getActionsByLink(linkToScrape).forEach(actionExecutor::executeAction);

        System.out.println("Done with the actions on the page .........");

        seleniumSetup.getWebDriver().close();
    }

    /**
     * The builder of the WebpageScraper
     */
    public static class WebpageScraperBuilder {
        private SeleniumSetup seleniumSetup;
        private ActionExecutor actionExecutor;
        private ActionFilter actionFilter;
        private RedisDao redisDao;

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
            return new WebpageScraper(this.seleniumSetup, this.redisDao,
                    this.actionExecutor, this.actionFilter);
        }
    }
}
