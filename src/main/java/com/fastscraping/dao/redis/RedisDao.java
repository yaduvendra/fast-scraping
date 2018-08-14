package com.fastscraping.dao.redis;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.models.ElementWithActions;
import org.redisson.api.RedissonClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fastscraping.util.JsonHelper.toJsonString;
import static com.fastscraping.util.JsonHelper.getObjectFromJson;

public class RedisDao implements ScraperDaoInf {

    private final RedissonConfig redissonConfig;
    private final RedissonClient redissonClient;

    public RedisDao(RedissonConfig redissonConfig) {
        this.redissonConfig = redissonConfig;
        this.redissonClient = this.redissonConfig.getRedissonClient();
    }

    @Override
    public List<Boolean> saveLinksToScrape(String key, List<String> links) {
        return links.stream()
                .map(link -> redissonClient.getSet(key).add(link))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getLinksToScrape(String key) {
        return redissonClient.getSet(key)
                .readAll().stream()
                .map(valueObj -> (String) valueObj)
                .collect(Collectors.toList());
    }

    public List<Boolean> setLinkToAction(String link, List<ElementWithActions> elementsWithActions)
            throws MalformedURLException {

        URL urlKey = new URL(link);
        return elementsWithActions
                .stream()
                .map(elementWithActions -> {
                    try {
                        String redisSetKey = urlKey.getHost() + urlKey.getPath(); //Only www.example.com + /path/to/some/
                        String jsonString = toJsonString(elementWithActions);
                        System.out.println("Going to store - " + jsonString + " for key - " + redisSetKey);

                        return redissonClient.getSet(redisSetKey).add(jsonString);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());

    }

    public List<Optional<ElementWithActions>> getElementsWithActionsByLink(String link) throws MalformedURLException {

        URL urlKey = new URL(link);

        System.out.println("Getting the elementWithActObjs with key - " + urlKey.getHost() + urlKey.getPath());

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
}
