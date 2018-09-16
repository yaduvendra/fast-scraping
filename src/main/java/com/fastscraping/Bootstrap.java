package com.fastscraping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fastscraping.dao.InMemoryDaoInf;
import com.fastscraping.dao.ScraperDao;
import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.dao.PersistentDaoInf;
import com.fastscraping.dao.mongo.MongoDao;
import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.dao.redis.RedissonConfig;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.scraper.ScrapeLinksPoller;
import com.fastscraping.scraper.WebDriverKeeper;
import com.fastscraping.scraper.WebpageScraper;
import com.fastscraping.util.JsonHelper;
import com.mongodb.ServerAddress;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;


public class Bootstrap {

    public static void main(String args[]) {

        System.setProperty("webdriver.gecko.driver", "/opt/geckodriver");

        final List<ServerAddress> mongoNodes = new LinkedList<>();
        mongoNodes.add(new ServerAddress("localhost"));

        PersistentDaoInf persistentDao = new MongoDao(mongoNodes);
        InMemoryDaoInf inMemoryDao = new RedisDao(new RedissonConfig());

        ScraperDaoInf scraperDao = new ScraperDao(inMemoryDao, persistentDao);
        WebpageScraper scraper = WebpageScraper.getSingletonWebpageScraper(scraperDao);

        try {

            File scrapingInformationJson = new File("/home/ashish/naukri.json");
            BufferedReader bufReader = new BufferedReader(new FileReader(scrapingInformationJson));

            ScrapeLinksPoller scrapeLinksPoller = ScrapeLinksPoller.getSingletonInstance(scraper, scraperDao);

            bufReader.lines().reduce((JSON, nextLine) -> JSON + nextLine + "\n").ifPresent(json -> {
                try {
                    ScrapingInformation scrapingInfo = JsonHelper.getScrapingInformationFromJson(json,
                            new TypeReference<ScrapingInformation>(){});

                    /** Index the ScrapingInformation in the DB */
                    scraperDao.addScrapingInforamtion(scrapingInfo);

                    /** Add/initialize the WebDrivers before starting the scraping */
                    WebDriverKeeper.addWebDrivers(scrapingInfo.getClientId(), scrapingInfo.getJobId(),
                            scrapingInfo.getNumberOfBrowsers());

                    /** Send the clientId and jobId to the ScrapeLinkPoller so that it polls the links to scrape */
                    scrapeLinksPoller.addClientJob(scrapingInfo.getClientId(), scrapingInfo.getJobId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread.sleep(1000000);

        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1269);
        } finally {
            scraperDao.closeDBConnections();
            System.exit(0);
        }
    }
}
