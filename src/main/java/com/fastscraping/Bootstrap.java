package com.fastscraping;

import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.dao.redis.RedissonConfig;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.scraper.*;
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

        RedisDao redisDao = new RedisDao(new RedissonConfig());

        try {

            File scrapingInformationJson = new File("/home/ashish/scraping_information.json");
            BufferedReader bufReader = new BufferedReader(new FileReader(scrapingInformationJson));

            ScrapeLinksPoller scrapeLinksPoller = ScrapeLinksPoller.getSingletonInstance(redisDao, 100);

            bufReader.lines().reduce((JSON, nextLine) -> JSON + nextLine + "\n").ifPresent(json -> {
                try {
                    ScrapingInformation scrapingInfo = JsonHelper.getObjectFromJson(json, ScrapingInformation.class);

                    /** Index the scrapingInformation in the Redis and clientId and jobId to the scrapeLinkPoller */
                    redisDao.indexScrapingInforamtion(scrapingInfo);
                    scrapeLinksPoller.addClientJob(scrapingInfo.getClientId(), scrapingInfo.getJobId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            System.exit(1);
        }
    }
}
