package com.fastscraping.scraper;

import com.fastscraping.models.ElementWithActions;
import org.openqa.selenium.JavascriptExecutor;

import java.net.MalformedURLException;
import java.util.List;

public class WebpageScraper {

    private final SeleniumSetup seleniumSetup;
    private final String linkToScrape;
    private final JavascriptExecutor jsExecutor;
    private final ActionExecutor actionExecutor;
    private final ActionFilter actionFilter;

    private WebpageScraper(SeleniumSetup seleniumSetup, String linkToScrape,
                           ActionExecutor actionExecutor, ActionFilter actionFilter) {
        this.seleniumSetup = seleniumSetup;
        this.linkToScrape = linkToScrape;
        this.actionExecutor = actionExecutor;
        this.actionFilter = actionFilter;

        this.jsExecutor = (JavascriptExecutor) (this.seleniumSetup.getWebDriver());
    }

    public void startScraping() throws MalformedURLException {

        System.out.println("Starting the scraping..........");

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
        private String linkToScrape;
        private ActionExecutor actionExecutor;
        private ActionFilter actionFilter;

        public WebpageScraperBuilder() {
            
        }

        public WebpageScraperBuilder setWebpage(SeleniumSetup seleniumSetup) {
            this.seleniumSetup = seleniumSetup;
            return this;
        }

        public WebpageScraperBuilder setLinkToScrape(String linkToScrape) {
            this.linkToScrape = linkToScrape;
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

        public WebpageScraper build() {
            return new WebpageScraper(this.seleniumSetup, this.linkToScrape, this.actionExecutor, this.actionFilter);
        }
    }
}
