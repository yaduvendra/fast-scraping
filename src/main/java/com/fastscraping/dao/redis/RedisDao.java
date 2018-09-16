package com.fastscraping.dao.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fastscraping.dao.InMemoryDaoInf;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.models.WebpageDetails;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fastscraping.util.Constants.*;
import static com.fastscraping.util.JsonHelper.getWebpageDetailsFromJson;
import static com.fastscraping.util.JsonHelper.toJsonString;

public class RedisDao implements InMemoryDaoInf {

    private final RedissonClient redissonClient;

    public RedisDao(RedissonConfig redissonConfig) {
        this.redissonClient = redissonConfig.getRedissonClient();
    }

    @Override
    public List<Boolean>
    addLinksToScrape(final String clientId, final String jobId, List<String> links) {
        return links.stream()
                .map(link -> {
                    try {
                        URL url = new URL(link);
                        return redissonClient
                                .getQueue(linksToScrapeSetName(clientId + jobId))
                                .add(url.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getLinksToScrape(final String clientId, final String jobId) {
        RQueue<String> queue = redissonClient.getQueue(linksToScrapeSetName(clientId + jobId));

        Set<String> scrapedLink = redissonClient.getSet(scrapedLinkSetName(clientId + jobId))
                .readAll()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        List<String> queueElements = queue.readAll();

        queueElements.removeAll(scrapedLink); //Don't scrape links which have already been scraped.

        queue.clear();

        return queueElements;
    }

    @Override
    public void addScrapingInforamtion(final ScrapingInformation scrapingInformation) {
        System.out.println("Going to add the Scraping Information to the Redis.");

        String key = scrapingInformation.getClientId() + "/" + scrapingInformation.getJobId();

        redissonClient.getMap(scrapingInformationMap).put(key, toJsonString(scrapingInformation.getWebpageDetails()));

        scrapingInformation.getRoots().forEach(root -> {
            try {
                URL url = new URL(root); //To validate the root URL

                System.out.println("The root link to scrape is -- " + url.toString() + " with key " +
                        scrapingInformation.getClientId() + scrapingInformation.getJobId());

                redissonClient
                        .getQueue(linksToScrapeSetName(scrapingInformation.getClientId() +
                                scrapingInformation.getJobId()))
                        .add(url.toString());
            } catch (MalformedURLException e) {
                System.out.println("The URL (" + root + ") given as root in scraping information is not a valid URL.");
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<ScrapingInformation> getScrapingInformation(String clientId, String jobId) {
        String jsonDoc = (String) redissonClient.getMap(scrapingInformationMap)
                .get(clientId + "/" + jobId);

        try {
            getWebpageDetailsFromJson(jsonDoc, new TypeReference<List<WebpageDetails>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        return null;
    }

    @Override
    public boolean addToScrapedLinks(final String link, String clientId, String jobId) {
        return redissonClient
                .getSet(scrapedLinkSetName(clientId + jobId))
                .add(link);
    }

    @Override
    public void closeDBConnection() {
        redissonClient.shutdown();
    }

}
