package com.fastscraping;

import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.dao.redis.RedissonConfig;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.scraper.*;
import com.fastscraping.util.JsonHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class Bootstrap {

    public static void main(String args[]) {

        System.setProperty("webdriver.gecko.driver", "/opt/geckodriver");

        ScraperDaoInf scraperDao = new RedisDao(new RedissonConfig());
        WebpageScraper scraper = WebpageScraper.getSingletonWebpageScraper(scraperDao);

        try {

            File scrapingInformationJson = new File("/home/ashish/scraping_information.json");
            BufferedReader bufReader = new BufferedReader(new FileReader(scrapingInformationJson));

            ScrapeLinksPoller scrapeLinksPoller = ScrapeLinksPoller.getSingletonInstance(scraper, scraperDao);

            bufReader.lines().reduce((JSON, nextLine) -> JSON + nextLine + "\n").ifPresent(json -> {
                try {
                    ScrapingInformation scrapingInfo = JsonHelper.getObjectFromJson(json, ScrapingInformation.class);

                    /** Add the WebDriver's to start the scraping */
                    WebDriverKeeper.addWebDrivers(scrapingInfo.getClientId(), scrapingInfo.getJobId(),
                            scrapingInfo.getNumberOfBrowsers());
                    /** Index the ScrapingInformation in the DB */
                    scraperDao.indexScrapingInforamtion(scrapingInfo);
                    /** Send the clientId and jobId to the ScrapeLinkPoller so that it polls the links to scrape */
                    scrapeLinksPoller.addClientJob(scrapingInfo.getClientId(), scrapingInfo.getJobId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread.sleep(1000000);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.exit(1);
        }
    }
}
