package com.fastscraping.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.models.ElementWithActions;
import com.fastscraping.util.JsonHelper;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionFilter {

    private final RedisDao redisDao;

    public ActionFilter(RedisDao redisDao){
        this.redisDao = redisDao;
    }

    public List<ElementWithActions> getActionsByLink(String link) throws MalformedURLException {

        System.out.println("Trying to get the elements from redis Dao");

        return redisDao.getElementsWithActionsByLink(link)
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<Boolean> addAcionsForLink(String link, List<ElementWithActions> elementsWithActions)
            throws MalformedURLException {
        try {
            System.out.println("Following is going to be saved in redis -- " + JsonHelper.toPrettyJsonString(elementsWithActions));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return redisDao.setLinkToAction(link, elementsWithActions);
    }

}
