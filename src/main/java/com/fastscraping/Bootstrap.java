package com.fastscraping;

import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.dao.redis.RedissonConfig;
import com.fastscraping.models.ActionName;
import com.fastscraping.models.ElementWithActions;
import com.fastscraping.scraper.ActionExecutor;
import com.fastscraping.scraper.ActionFilter;
import com.fastscraping.scraper.SeleniumSetup;
import com.fastscraping.scraper.WebpageScraper;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import static com.fastscraping.models.ActionName.GRAB_LINKS_TO_SCRAPE;
import static com.fastscraping.scraper.WebpageScraper.WebpageScraperBuilder;

public class Bootstrap {

    public static void main(String args[]) {

        System.setProperty("webdriver.gecko.driver", "/opt/geckodriver");

        WebDriver webDriver = null;

        try {
            webDriver = new FirefoxDriver();

            String rootURL = "https://www.hdwallpapers.net/";

            RedisDao redisDao = new RedisDao(new RedissonConfig());

            ActionExecutor actionExecutor = new ActionExecutor.ActionExecutorBuilder()
                    .setDriver(webDriver)
                    .setJSExecutor((JavascriptExecutor) webDriver)
                    .setScraperDao(redisDao)
                    .setRootURL(rootURL)
                    .build();

            ActionFilter actionFilter = new ActionFilter(redisDao);

            WebpageScraper scraper = new WebpageScraperBuilder()
                    .setWebpage(new SeleniumSetup(webDriver))
                    .setLinkToScrape(rootURL)
                    .setActionExecutor(actionExecutor)
                    .setActionFilter(actionFilter)
                    .build();

            List<ElementWithActions> elementsWithActions = new LinkedList<>();

            LinkedList<ActionName> grabLinksAction = new LinkedList<>();
            grabLinksAction.add(GRAB_LINKS_TO_SCRAPE);

            elementsWithActions.add(new ElementWithActions.ElementWithActionsBuilder()
                    .setSelector("div.uk-slidenav-position")
                    .setActions(grabLinksAction)
                    .build()
            );

            actionFilter.addAcionsForLink(rootURL, elementsWithActions);

            scraper.startScraping();

        } catch (InvalidArgumentException | MalformedURLException ex) {
            if (webDriver != null) {
                webDriver.close();
            }
            System.out.println("There is an error while trying to access the link provided for this scraper. " +
                    ex.getMessage());
            System.exit(-1);
        } finally {
            System.exit(0);
        }
    }
}
