package com.fastscraping;

import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.dao.redis.RedissonConfig;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.scraper.ActionExecutor;
import com.fastscraping.scraper.ActionFilter;
import com.fastscraping.scraper.SeleniumSetup;
import com.fastscraping.scraper.WebpageScraper;
import com.fastscraping.util.JsonHelper;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static com.fastscraping.scraper.ActionExecutor.ActionExecutorBuilder;
import static com.fastscraping.scraper.WebpageScraper.WebpageScraperBuilder;

public class Bootstrap {

    public static void main(String args[]) {

        System.setProperty("webdriver.gecko.driver", "/opt/geckodriver");

        WebDriver webDriver = null;

        RedisDao redisDao = new RedisDao(new RedissonConfig());

        try {
            webDriver = new FirefoxDriver();

            ActionExecutor actionExecutor = new ActionExecutorBuilder()
                    .setDriver(webDriver)
                    .build();

            ActionFilter actionFilter = new ActionFilter(redisDao);

            WebpageScraper scraper = new WebpageScraperBuilder()
                    .setWebpage(new SeleniumSetup(webDriver))
                    .setActionExecutor(actionExecutor)
                    .setActionFilter(actionFilter)
                    .setRedisDao(redisDao)
                    .build();

            File scrapingInformationJson = new File("/home/ashish/scraping_information.json");
            BufferedReader bufReader = new BufferedReader(new FileReader(scrapingInformationJson));


            bufReader.lines().reduce((JSON, nextLine) -> JSON + nextLine + "\n").ifPresent(json -> {
                try {
                    ScrapingInformation scrapingInfo = JsonHelper.getObjectFromJson(json, ScrapingInformation.class);
                    //TODO: Add the scraping information to the Redis and (NoSQL/SQL DB, when persist database is setup)
                    //TODO: Send the clientId and Job ID to the ScrapeLinksPoller
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


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
