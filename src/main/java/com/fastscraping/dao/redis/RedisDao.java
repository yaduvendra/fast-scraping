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

import static com.fastscraping.util.JsonHelper.getObjectFromJson;
import static com.fastscraping.util.JsonHelper.toJsonString;
import static com.fastscraping.util.JsonHelper.toPrettyJsonString;

public class RedisDao implements ScraperDaoInf {

    private final RedissonConfig redissonConfig;
    private final RedissonClient redissonClient;

    public RedisDao(RedissonConfig redissonConfig) {
        this.redissonConfig = redissonConfig;
        this.redissonClient = this.redissonConfig.getRedissonClient();
    }

    @Override
    public List<Boolean> saveLinksToScrape(final String key, List<String> links) {
        return links.stream()
                .map(link -> {
                    synchronized (redissonClient) {
                        return redissonClient.getSet(key).add(link);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getLinksToScrape(final String key) {
        synchronized (redissonClient) {
            return redissonClient.getSet(key)
                    .readAll().stream()
                    .map(valueObj -> (String) valueObj)
                    .collect(Collectors.toList());
        }
    }

    public List<Boolean> setLinkToAction(final String link, final List<ElementWithActions> elementsWithActions)
            throws MalformedURLException {

        URL urlKey = new URL(link);
        return elementsWithActions
                .stream()
                .map(elementWithActions -> {
                    String redisSetKey = urlKey.getHost() + urlKey.getPath(); //Only www.example.com + /some/path/
                    String jsonString = toJsonString(elementWithActions);

                    return redissonClient.getSet(redisSetKey).add(jsonString);
                })
                .collect(Collectors.toList());

    }

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

    public void indexScrapingInforamtion(final ScrapingInformation scrapingInformation,
                                         final String clientId, final String jobId) {
        scrapingInformation.getWebpages().forEach(webpage -> {
            if (webpage.getElementWithActions() != null) {
                if (webpage.getUrlRegex() != null) {
                    redissonClient.getMap(Constants.getUrlRegexMapName(clientId + jobId))
                            .put(webpage.getUrlRegex(), toPrettyJsonString(webpage.getElementWithActions()));
                }

                if (webpage.getUniqueTag() != null) {
                    redissonClient.getMap(Constants.getUniqueTagMapName(clientId + jobId))
                            .put(webpage.getUniqueTag(), toPrettyJsonString(webpage.getElementWithActions()));
                }

                if (webpage.getUniqueStringOnPage() != null) {
                    redissonClient.getMap(Constants.getUniqueStringMapName(clientId + jobId))
                            .put(webpage.getUniqueStringOnPage(), toPrettyJsonString(webpage.getElementWithActions()));
                }
            }
        });
    }

}
