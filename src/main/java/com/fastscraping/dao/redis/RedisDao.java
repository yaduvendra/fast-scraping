package com.fastscraping.dao.redis;

import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.models.ElementWithActions;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.util.Constants;
import org.redisson.api.RedissonClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fastscraping.util.JsonHelper.*;

public class RedisDao implements ScraperDaoInf {

    private final RedissonConfig redissonConfig;
    private final RedissonClient redissonClient;

    public RedisDao(RedissonConfig redissonConfig) {
        this.redissonConfig = redissonConfig;
        this.redissonClient = this.redissonConfig.getRedissonClient();
    }

    @Override
    public List<Boolean> saveLinksToScrape(final String clientId, final String jobId, List<String> links) {
        return links.stream()
                .map(link -> {
                    synchronized (redissonClient) {
                        return redissonClient.getSet(Constants.linksToScrapeSetName(clientId + jobId)).add(link);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getLinksToScrape(final String clientId, final String jobId) {
        return redissonClient.getSet(Constants.linksToScrapeSetName(clientId + jobId))
                .readAll().stream()
                .map(valueObj -> (String) valueObj)
                .collect(Collectors.toList());
    }

    @Override
    public int getNumberOfBrowser(String clientId, String jobId) {
        return 0;
    }

    @Override
    public List<Optional<ElementWithActions>> getElementsWithActionsByLink(final String link)
            throws MalformedURLException {

        URL urlKey = new URL(link);

        return redissonClient
                .getSet(urlKey.getHost() + urlKey.getPath()).readAll()
                .stream()
                .map(elementWithActObj -> {
                    try {
                        return Optional.of(getObjectFromJson(elementWithActObj.toString(), ElementWithActions.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Optional.<ElementWithActions>empty();
                    }
                })
                .collect(Collectors.toList());
    }

    public void indexScrapingInforamtion(final ScrapingInformation scrapingInformation) {
        scrapingInformation.getWebpages().forEach(webpage -> {
            if (webpage.getElementWithActions() != null) {
                if (webpage.getUrlRegex() != null) {
                    redissonClient
                            .getMap(Constants.getUrlRegexMapName(scrapingInformation.getClientId() +
                                    scrapingInformation.getJobId()))
                            .put(webpage.getUrlRegex(), toPrettyJsonString(webpage.getElementWithActions()));
                }

                if (webpage.getUniqueTag() != null) {
                    redissonClient
                            .getMap(Constants.getUniqueTagMapName(scrapingInformation.getClientId() +
                                    scrapingInformation.getJobId()))
                            .put(webpage.getUniqueTag(), toPrettyJsonString(webpage.getElementWithActions()));
                }

                if (webpage.getUniqueStringOnPage() != null) {
                    redissonClient
                            .getMap(Constants.getUniqueStringMapName(scrapingInformation.getClientId() +
                                    scrapingInformation.getJobId()))
                            .put(webpage.getUniqueStringOnPage(), toPrettyJsonString(webpage.getElementWithActions()));
                }
            }
        });

        scrapingInformation.getRoots().forEach(root -> {
            try {
                URL url = new URL(root); //To validate the root URL
                redissonClient
                        .getSet(Constants.linksToScrapeSetName(scrapingInformation.getClientId() +
                                scrapingInformation.getJobId()))
                        .add(url.getHost() + url.getPath());
            } catch (MalformedURLException e) {
                System.out.println("The URL (" + root + ") given as root in scraping information is not a valid URL.");
                e.printStackTrace();
            }
        });
    }

}
