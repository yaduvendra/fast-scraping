package com.fastscraping;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.dao.redis.RedissonConfig;
import com.fastscraping.models.ActionName;
import com.fastscraping.models.ElementWithActions;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.scraper.ActionExecutor;
import com.fastscraping.scraper.ActionFilter;
import com.fastscraping.scraper.SeleniumSetup;
import com.fastscraping.scraper.WebpageScraper;
import com.fastscraping.util.JsonHelper;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
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
/*
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
*/

            File scrapingInformationJson = new File("/home/ashish/scraping_information.json");
            BufferedReader bufReader = new BufferedReader(new FileReader(scrapingInformationJson));


            bufReader.lines().reduce((x, y) -> x + y + "\n").ifPresent(json -> {
                try {
                    ScrapingInformation scrapingInfo = JsonHelper.getObjectFromJson(json, ScrapingInformation.class);
                    System.out.println("Number of roots are - " + scrapingInfo.getRoots().size());
                    System.out.println("Number of web pages are - " + scrapingInfo.getWebpages().size());
                    System.out.println("The JSON serialized is -- " + JsonHelper.toPrettyJsonString(scrapingInfo));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


//            scraper.startScraping();

        } catch (InvalidArgumentException ex) {
            if (webDriver != null) {
                webDriver.close();
            }
            System.out.println("There is an error while trying to access the link provided for this scraper. " +
                    ex.getMessage());
            System.exit(-1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            System.exit(1);
        }
    }
}
